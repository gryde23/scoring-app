"""
Credit Scoring — обучение двух моделей LightGBM
  1. Риск дефолта       → предсказывает вероятность [0..1]
  2. Кредитный лимит    → предсказывает лимит, кратный 5 000 ₽

Входные файлы:
  credit_scoring_dataset_v4.csv

Выходные файлы:
  model_default_risk.pkl
  model_credit_limit.pkl
  ordinal_encoder.pkl
  model1_default_risk.png
  model2_credit_limit.png
"""

import os
import warnings

import joblib
import lightgbm as lgb
import matplotlib.pyplot as plt
import numpy as np
import pandas as pd
import seaborn as sns
from sklearn.calibration import calibration_curve
from sklearn.metrics import (
    average_precision_score,
    mean_absolute_error,
    mean_squared_error,
    precision_recall_curve,
    r2_score,
    roc_auc_score,
    roc_curve,
)
from sklearn.model_selection import KFold, train_test_split
from sklearn.preprocessing import OrdinalEncoder

warnings.filterwarnings("ignore")
plt.rcParams["figure.dpi"] = 130
plt.rcParams["font.family"] = "DejaVu Sans"
sns.set_theme(style="whitegrid", palette="muted")

# ─────────────────────────────────────────────
# КОНФИГ
# ─────────────────────────────────────────────

SEED        = 42
DATA_PATH   = "credit_scoring_dataset.csv"
MODELS_DIR  = "models"     # pkl-файлы моделей
REPORTS_DIR = "reports"    # графики
N_FOLDS     = 5
TEST_SIZE   = 0.2
LIMIT_ROUND = 5_000        # лимит кратен этому значению

CAT_COLS = [
    "marital_status",
    "education",
    "region",
    "employment_type",
    "card_type_requested",
]

os.makedirs(MODELS_DIR,  exist_ok=True)
os.makedirs(REPORTS_DIR, exist_ok=True)

def save_fig(name: str) -> None:
    """Сохраняет текущую фигуру в REPORTS_DIR и закрывает её."""
    path = os.path.join(REPORTS_DIR, name)
    plt.savefig(path, bbox_inches="tight")
    plt.close()
    print(f"    ✅  {name}")

# ─────────────────────────────────────────────
# 1. ЗАГРУЗКА И ПОДГОТОВКА
# ─────────────────────────────────────────────

print("📂  Загрузка датасета...")
df = pd.read_csv(DATA_PATH)
print(f"    Shape: {df.shape}")

# Кодируем категории числами (LightGBM понимает categorical_feature)
encoder = OrdinalEncoder(handle_unknown="use_encoded_value", unknown_value=-1)
df[CAT_COLS] = encoder.fit_transform(df[CAT_COLS]).astype(int)
joblib.dump(encoder, os.path.join(MODELS_DIR, "ordinal_encoder.pkl"))

FEATURES = [c for c in df.columns if c not in ["default_probability", "credit_limit"]]

# ── Датасет 1: риск дефолта (все строки) ──
X_def = df[FEATURES]
y_def = df["default_probability"]

# ── Датасет 2: лимит (только одобренные, credit_limit > 0) ──
df_lim = df[df["credit_limit"] > 0].copy()
X_lim  = df_lim[FEATURES]
y_lim  = df_lim["credit_limit"]

X_def_tr, X_def_te, y_def_tr, y_def_te = train_test_split(
    X_def, y_def, test_size=TEST_SIZE, random_state=SEED
)
X_lim_tr, X_lim_te, y_lim_tr, y_lim_te = train_test_split(
    X_lim, y_lim, test_size=TEST_SIZE, random_state=SEED
)

print(f"    Дефолт  — train: {len(X_def_tr):,} | test: {len(X_def_te):,}")
print(f"    Лимит   — train: {len(X_lim_tr):,} | test: {len(X_lim_te):,}")

kf = KFold(n_splits=N_FOLDS, shuffle=True, random_state=SEED)

# ─────────────────────────────────────────────
# 2. ОБУЧЕНИЕ — МОДЕЛЬ 1: РИСК ДЕФОЛТА
# ─────────────────────────────────────────────

print("\n🎯  Модель 1 — Риск дефолта")

params_def = {
    "objective":         "regression",
    "metric":            "rmse",
    "boosting_type":     "gbdt",
    "num_leaves":        63,
    "learning_rate":     0.05,
    "n_estimators":      1000,
    "feature_fraction":  0.8,
    "bagging_fraction":  0.8,
    "bagging_freq":      5,
    "min_child_samples": 30,
    "lambda_l1":         0.1,
    "lambda_l2":         0.1,
    "random_state":      SEED,
    "verbose":           -1,
}

cv_rmse_def, cv_r2_def, best_iters_def = [], [], []

for fold, (ti, vi) in enumerate(kf.split(X_def_tr)):
    X_tr, X_val = X_def_tr.iloc[ti], X_def_tr.iloc[vi]
    y_tr, y_val = y_def_tr.iloc[ti], y_def_tr.iloc[vi]

    model_fold = lgb.train(
        params_def,
        lgb.Dataset(X_tr, y_tr, categorical_feature=CAT_COLS),
        num_boost_round=1000,
        valid_sets=[lgb.Dataset(X_val, y_val, categorical_feature=CAT_COLS)],
        callbacks=[
            lgb.early_stopping(50, verbose=False),
            lgb.log_evaluation(-1),
        ],
    )

    preds = np.clip(model_fold.predict(X_val), 0, 1)
    rmse  = np.sqrt(mean_squared_error(y_val, preds))
    r2    = r2_score(y_val, preds)
    cv_rmse_def.append(rmse)
    cv_r2_def.append(r2)
    best_iters_def.append(model_fold.best_iteration)
    print(f"    Fold {fold+1}  RMSE={rmse:.4f}  R²={r2:.4f}  iter={model_fold.best_iteration}")

print(f"\n    CV RMSE: {np.mean(cv_rmse_def):.4f} ± {np.std(cv_rmse_def):.4f}")
print(f"    CV R²:   {np.mean(cv_r2_def):.4f} ± {np.std(cv_r2_def):.4f}")

# Финальная модель на всём трейне
model_def = lgb.train(
    params_def,
    lgb.Dataset(X_def_tr, y_def_tr, categorical_feature=CAT_COLS),
    num_boost_round=int(np.mean(best_iters_def)),
    callbacks=[lgb.log_evaluation(-1)],
)

test_preds_def = np.clip(model_def.predict(X_def_te), 0, 1)
y_def_te_bin   = (y_def_te > 0.5).astype(int)

test_rmse_def = np.sqrt(mean_squared_error(y_def_te, test_preds_def))
test_r2_def   = r2_score(y_def_te, test_preds_def)
test_auc_def  = roc_auc_score(y_def_te_bin, test_preds_def)
test_ap_def   = average_precision_score(y_def_te_bin, test_preds_def)

print(f"\n    Test RMSE: {test_rmse_def:.4f}")
print(f"    Test R²:   {test_r2_def:.4f}")
print(f"    Test AUC:  {test_auc_def:.4f}")
print(f"    Test AP:   {test_ap_def:.4f}")

joblib.dump(model_def, os.path.join(MODELS_DIR, "model_default_risk.pkl"))
print("    ✅  model_default_risk.pkl сохранён")

# ─────────────────────────────────────────────
# 3. ОБУЧЕНИЕ — МОДЕЛЬ 2: КРЕДИТНЫЙ ЛИМИТ
# ─────────────────────────────────────────────

print("\n💳  Модель 2 — Кредитный лимит")

params_lim = {
    "objective":         "regression",
    "metric":            "mae",
    "boosting_type":     "gbdt",
    "num_leaves":        63,
    "learning_rate":     0.05,
    "n_estimators":      1000,
    "feature_fraction":  0.8,
    "bagging_fraction":  0.8,
    "bagging_freq":      5,
    "min_child_samples": 30,
    "lambda_l1":         0.5,
    "lambda_l2":         0.5,
    "random_state":      SEED,
    "verbose":           -1,
}

cv_mae_lim, cv_r2_lim, best_iters_lim = [], [], []

for fold, (ti, vi) in enumerate(kf.split(X_lim_tr)):
    X_tr, X_val = X_lim_tr.iloc[ti], X_lim_tr.iloc[vi]
    y_tr, y_val = y_lim_tr.iloc[ti], y_lim_tr.iloc[vi]

    model_fold = lgb.train(
        params_lim,
        lgb.Dataset(X_tr, y_tr, categorical_feature=CAT_COLS),
        num_boost_round=1000,
        valid_sets=[lgb.Dataset(X_val, y_val, categorical_feature=CAT_COLS)],
        callbacks=[
            lgb.early_stopping(50, verbose=False),
            lgb.log_evaluation(-1),
        ],
    )

    preds = model_fold.predict(X_val)
    mae   = mean_absolute_error(y_val, preds)
    r2    = r2_score(y_val, preds)
    cv_mae_lim.append(mae)
    cv_r2_lim.append(r2)
    best_iters_lim.append(model_fold.best_iteration)
    print(f"    Fold {fold+1}  MAE={mae:,.0f} ₽  R²={r2:.4f}  iter={model_fold.best_iteration}")

print(f"\n    CV MAE: {np.mean(cv_mae_lim):,.0f} ± {np.std(cv_mae_lim):,.0f} ₽")
print(f"    CV R²:  {np.mean(cv_r2_lim):.4f} ± {np.std(cv_r2_lim):.4f}")

model_lim = lgb.train(
    params_lim,
    lgb.Dataset(X_lim_tr, y_lim_tr, categorical_feature=CAT_COLS),
    num_boost_round=int(np.mean(best_iters_lim)),
    callbacks=[lgb.log_evaluation(-1)],
)


def predict_limit(model, X):
    """Предсказывает лимит и округляет до ближайшего LIMIT_ROUND."""
    raw = model.predict(X)
    rounded = np.round(raw / LIMIT_ROUND) * LIMIT_ROUND
    return rounded.astype(int)


test_preds_lim_raw     = model_lim.predict(X_lim_te)
test_preds_lim_rounded = predict_limit(model_lim, X_lim_te)

# Проверка кратности
assert np.all(test_preds_lim_rounded % LIMIT_ROUND == 0), \
    "❌ Найдены значения, не кратные 5 000!"
print(f"\n    ✅ Все предсказания кратны {LIMIT_ROUND:,} ₽")

test_mae_lim  = mean_absolute_error(y_lim_te, test_preds_lim_rounded)
test_rmse_lim = np.sqrt(mean_squared_error(y_lim_te, test_preds_lim_rounded))
test_r2_lim   = r2_score(y_lim_te, test_preds_lim_rounded)

pct_err   = np.abs(y_lim_te.values - test_preds_lim_rounded) / y_lim_te.values * 100
within_10 = float((pct_err <= 10).mean() * 100)
within_20 = float((pct_err <= 20).mean() * 100)
within_30 = float((pct_err <= 30).mean() * 100)

print(f"    Test MAE:  {test_mae_lim:,.0f} ₽")
print(f"    Test RMSE: {test_rmse_lim:,.0f} ₽")
print(f"    Test R²:   {test_r2_lim:.4f}")
print(f"    В пределах 10%: {within_10:.1f}%")
print(f"    В пределах 20%: {within_20:.1f}%")

# Декодируем тип карты для графика
card_cats  = encoder.categories_[CAT_COLS.index("card_type_requested")]
card_names = [card_cats[int(c)] for c in X_lim_te["card_type_requested"].values]

joblib.dump(model_lim, os.path.join(MODELS_DIR, "model_credit_limit.pkl"))
print("    ✅  model_credit_limit.pkl сохранён")

# ─────────────────────────────────────────────
# 4. ГРАФИКИ — МОДЕЛЬ 1: РИСК ДЕФОЛТА
# ─────────────────────────────────────────────

print("\n📊  Графики — Модель 1 (риск дефолта)")

FIG_W, FIG_H = 10, 6   # размер каждого отдельного графика

# 4.1  Feature importance
fig, ax = plt.subplots(figsize=(FIG_W, 8))
imp = (
    pd.DataFrame({"feature": FEATURES, "importance": model_def.feature_importance("gain")})
    .sort_values("importance")
    .tail(20)
)
bars = ax.barh(imp["feature"], imp["importance"], color=sns.color_palette("Blues_d", len(imp)))
ax.set_title("Модель 1 — Feature Importance (gain), топ 20", fontsize=13, fontweight="bold")
ax.set_xlabel("Gain")
for bar, val in zip(bars, imp["importance"]):
    ax.text(val * 1.01, bar.get_y() + bar.get_height() / 2, f"{val:,.0f}", va="center", fontsize=8)
ax.margins(x=0.15)
save_fig("def_01_feature_importance.png")

# 4.3  Predicted vs Real
fig, ax = plt.subplots(figsize=(FIG_W, FIG_H))
ax.scatter(y_def_te, test_preds_def, alpha=0.1, s=4, color="steelblue", rasterized=True)
ax.plot([0, 1], [0, 1], "r--", lw=1.5, label="Идеал")
ax.set_title("Модель 1 — Предсказанное vs Реальное", fontsize=13, fontweight="bold")
ax.set_xlabel("Реальная вероятность")
ax.set_ylabel("Предсказанная вероятность")
ax.legend(fontsize=10)
ax.text(0.05, 0.88,
        f"R²   = {test_r2_def:.4f}\nRMSE = {test_rmse_def:.4f}",
        transform=ax.transAxes, fontsize=11, bbox=dict(fc="white", ec="gray", alpha=0.8))
save_fig("def_03_predicted_vs_real.png")

# 4.4  Распределение скора по классам
fig, ax = plt.subplots(figsize=(FIG_W, FIG_H))
ax.hist(test_preds_def[y_def_te_bin == 0], bins=50, alpha=0.6, color="steelblue", label="Одобрен", density=True)
ax.hist(test_preds_def[y_def_te_bin == 1], bins=50, alpha=0.6, color="tomato",    label="Дефолт",  density=True)
ax.axvline(0.5, color="black",  ls="--", lw=1.5, label="Порог 0.5")
ax.axvline(0.7, color="orange", ls="--", lw=1.5, label="Порог 0.7")
ax.set_title("Модель 1 — Распределение скора по классам", fontsize=13, fontweight="bold")
ax.set_xlabel("Предсказанная вероятность дефолта")
ax.set_ylabel("Плотность")
ax.legend(fontsize=10)
save_fig("def_04_score_distribution.png")

# 4.5  ROC-кривая
fig, ax = plt.subplots(figsize=(FIG_W, FIG_H))
fpr, tpr, _ = roc_curve(y_def_te_bin, test_preds_def)
ax.plot(fpr, tpr, color="steelblue", lw=2, label=f"AUC = {test_auc_def:.4f}")
ax.plot([0, 1], [0, 1], "r--", lw=1.5, label="Случайный классификатор")
ax.fill_between(fpr, tpr, alpha=0.1, color="steelblue")
ax.set_title("Модель 1 — ROC-кривая", fontsize=13, fontweight="bold")
ax.set_xlabel("False Positive Rate")
ax.set_ylabel("True Positive Rate")
ax.legend(fontsize=10)
save_fig("def_05_roc_curve.png")

# 4.6  Precision-Recall кривая
fig, ax = plt.subplots(figsize=(FIG_W, FIG_H))
prec, rec, _ = precision_recall_curve(y_def_te_bin, test_preds_def)
ax.plot(rec, prec, color="darkorange", lw=2, label=f"Average Precision = {test_ap_def:.4f}")
ax.fill_between(rec, prec, alpha=0.1, color="darkorange")
ax.set_title("Модель 1 — Precision-Recall кривая", fontsize=13, fontweight="bold")
ax.set_xlabel("Recall")
ax.set_ylabel("Precision")
ax.legend(fontsize=10)
save_fig("def_06_precision_recall.png")

# 4.8  Остатки
fig, ax = plt.subplots(figsize=(FIG_W, FIG_H))
res = y_def_te.values - test_preds_def
ax.hist(res, bins=60, color="mediumpurple", alpha=0.8, edgecolor="white")
ax.axvline(0, color="red", ls="--", lw=1.5, label="Нулевая ошибка")
ax.set_title("Модель 1 — Распределение остатков", fontsize=13, fontweight="bold")
ax.set_xlabel("Реальное − Предсказанное")
ax.set_ylabel("Количество")
ax.legend(fontsize=10)
ax.text(0.65, 0.88,
        f"μ = {res.mean():.4f}\nσ = {res.std():.4f}",
        transform=ax.transAxes, fontsize=11, bbox=dict(fc="white", ec="gray", alpha=0.8))
save_fig("def_08_residuals.png")

# ─────────────────────────────────────────────
# 5. ГРАФИКИ — МОДЕЛЬ 2: КРЕДИТНЫЙ ЛИМИТ
# ─────────────────────────────────────────────

print("\n📊  Графики — Модель 2 (кредитный лимит)")

y2  = y_lim_te.values
tp2 = test_preds_lim_rounded

# 5.1  Feature importance
fig, ax = plt.subplots(figsize=(FIG_W, 8))
imp = (
    pd.DataFrame({"feature": FEATURES, "importance": model_lim.feature_importance("gain")})
    .sort_values("importance")
    .tail(20)
)
bars = ax.barh(imp["feature"], imp["importance"], color=sns.color_palette("Greens_d", len(imp)))
ax.set_title("Модель 2 — Feature Importance (gain), топ 20", fontsize=13, fontweight="bold")
ax.set_xlabel("Gain")
for bar, val in zip(bars, imp["importance"]):
    ax.text(val * 1.01, bar.get_y() + bar.get_height() / 2, f"{val:,.0f}", va="center", fontsize=8)
ax.margins(x=0.15)
save_fig("lim_01_feature_importance.png")

# 5.3  Predicted vs Real
fig, ax = plt.subplots(figsize=(FIG_W, FIG_H))
ax.scatter(y2 / 1000, tp2 / 1000, alpha=0.1, s=4, color="seagreen", rasterized=True)
lm = max(y2.max(), tp2.max()) / 1000
ax.plot([0, lm], [0, lm], "r--", lw=1.5, label="Идеал")
ax.set_title("Модель 2 — Предсказанное vs Реальное", fontsize=13, fontweight="bold")
ax.set_xlabel("Реальный лимит (тыс. ₽)")
ax.set_ylabel("Предсказанный лимит (тыс. ₽)")
ax.legend(fontsize=10)
ax.text(0.05, 0.88,
        f"R²  = {test_r2_lim:.4f}\nMAE = {test_mae_lim/1000:,.1f}k ₽",
        transform=ax.transAxes, fontsize=11, bbox=dict(fc="white", ec="gray", alpha=0.8))
save_fig("lim_03_predicted_vs_real.png")

# 5.4  Распределение реального и предсказанного лимита
fig, ax = plt.subplots(figsize=(FIG_W, FIG_H))
ax.hist(y2  / 1000, bins=60, alpha=0.6, color="seagreen", label="Реальный",      density=True)
ax.hist(tp2 / 1000, bins=60, alpha=0.6, color="orange",   label="Предсказанный", density=True)
ax.set_title("Модель 2 — Распределение лимитов", fontsize=13, fontweight="bold")
ax.set_xlabel("Лимит (тыс. ₽)")
ax.set_ylabel("Плотность")
ax.legend(fontsize=10)
save_fig("lim_04_limit_distribution.png")

# 5.5  Остатки
fig, ax = plt.subplots(figsize=(FIG_W, FIG_H))
res = y2 - tp2
ax.hist(res / 1000, bins=60, color="mediumpurple", alpha=0.8, edgecolor="white")
ax.axvline(0, color="red", ls="--", lw=1.5, label="Нулевая ошибка")
ax.set_title("Модель 2 — Распределение остатков", fontsize=13, fontweight="bold")
ax.set_xlabel("Реальное − Предсказанное (тыс. ₽)")
ax.set_ylabel("Количество")
ax.legend(fontsize=10)
ax.text(0.60, 0.88,
        f"μ = {res.mean()/1000:,.1f}k ₽\nσ = {res.std()/1000:,.1f}k ₽",
        transform=ax.transAxes, fontsize=11, bbox=dict(fc="white", ec="gray", alpha=0.8))
save_fig("lim_05_residuals.png")

# 5.6  MAE по диапазонам лимита
fig, ax = plt.subplots(figsize=(FIG_W, FIG_H))
bins_l = [0, 100_000, 250_000, 500_000, 1_000_001]
lbls   = ["0–100k", "100–250k", "250–500k", "500k–1M"]
bidx   = pd.cut(y2, bins=bins_l, labels=lbls)
mae_b  = [mean_absolute_error(y2[bidx == l], tp2[bidx == l]) for l in lbls]
brs    = ax.bar(lbls, [v / 1000 for v in mae_b], color=sns.color_palette("Greens_d", 4), edgecolor="white")
ax.set_title("Модель 2 — MAE по диапазонам лимита", fontsize=13, fontweight="bold")
ax.set_xlabel("Диапазон лимита")
ax.set_ylabel("MAE (тыс. ₽)")
for b, v in zip(brs, mae_b):
    ax.text(b.get_x() + b.get_width() / 2, b.get_height() + 0.2,
            f"{v/1000:,.1f}k", ha="center", fontsize=11)
save_fig("lim_06_mae_by_range.png")

# 5.7  Средний лимит по типу карты (реальный vs предсказанный)
fig, ax = plt.subplots(figsize=(FIG_W, FIG_H))
card_df = pd.DataFrame({"card": card_names, "real": y2 / 1000, "pred": tp2 / 1000})
order   = ["standard", "gold", "platinum"]
rms     = [card_df[card_df.card == c]["real"].mean() for c in order]
pms     = [card_df[card_df.card == c]["pred"].mean() for c in order]
xp, w   = np.arange(len(order)), 0.35
ax.bar(xp - w / 2, rms, w, label="Реальный",      color="seagreen", alpha=0.85, edgecolor="white")
ax.bar(xp + w / 2, pms, w, label="Предсказанный", color="orange",   alpha=0.85, edgecolor="white")
ax.set_title("Модель 2 — Средний лимит по типу карты", fontsize=13, fontweight="bold")
ax.set_xlabel("Тип карты")
ax.set_ylabel("Средний лимит (тыс. ₽)")
ax.set_xticks(xp)
ax.set_xticklabels(order)
ax.legend(fontsize=10)
save_fig("lim_07_limit_by_card_type.png")

# 5.8  Доля предсказаний в пределах погрешности
fig, ax = plt.subplots(figsize=(FIG_W, FIG_H))
pct  = np.abs(y2 - tp2) / y2 * 100
vals = [float((pct <= t).mean() * 100) for t in [10, 20, 30]]
brs2 = ax.bar(["≤ 10%", "≤ 20%", "≤ 30%"], vals,
              color=["#2ecc71", "#f39c12", "#e74c3c"], edgecolor="white", width=0.45)
ax.set_title("Модель 2 — Доля предсказаний в пределах погрешности", fontsize=13, fontweight="bold")
ax.set_ylabel("% предсказаний")
ax.set_ylim(0, 115)
for b, v in zip(brs2, vals):
    ax.text(b.get_x() + b.get_width() / 2, b.get_height() + 1.5,
            f"{v:.1f}%", ha="center", fontsize=13, fontweight="bold")
save_fig("lim_08_error_tolerance.png")

# ─────────────────────────────────────────────
# 6. ИТОГ
# ─────────────────────────────────────────────

print("\n" + "=" * 55)
print("  ИТОГОВЫЕ МЕТРИКИ")
print("=" * 55)
print(f"\n  Модель 1 — Риск дефолта")
print(f"    CV  RMSE : {np.mean(cv_rmse_def):.4f} ± {np.std(cv_rmse_def):.4f}")
print(f"    CV  R²   : {np.mean(cv_r2_def):.4f} ± {np.std(cv_r2_def):.4f}")
print(f"    Test RMSE: {test_rmse_def:.4f}")
print(f"    Test R²  : {test_r2_def:.4f}")
print(f"    Test AUC : {test_auc_def:.4f}")
print(f"    Test AP  : {test_ap_def:.4f}")
print(f"\n  Модель 2 — Кредитный лимит (округлён до {LIMIT_ROUND:,} ₽)")
print(f"    CV  MAE  : {np.mean(cv_mae_lim):,.0f} ± {np.std(cv_mae_lim):,.0f} ₽")
print(f"    CV  R²   : {np.mean(cv_r2_lim):.4f} ± {np.std(cv_r2_lim):.4f}")
print(f"    Test MAE : {test_mae_lim:,.0f} ₽")
print(f"    Test RMSE: {test_rmse_lim:,.0f} ₽")
print(f"    Test R²  : {test_r2_lim:.4f}")
print(f"    В пределах 10%: {within_10:.1f}%")
print(f"    В пределах 20%: {within_20:.1f}%")
print(f"    В пределах 30%: {within_30:.1f}%")
print("=" * 55)