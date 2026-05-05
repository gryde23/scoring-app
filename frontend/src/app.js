import { createAdminRoutes } from "./admin.js";

const STORAGE_KEY = "scoringAppAuth";

const enums = {
  maritalStatus: [
    ["WIDOWED", "Вдовец / вдова"],
    ["MARRIED", "В браке"],
    ["DIVORCED", "В разводе"],
    ["SINGLE", "Не в браке"]
  ],
  education: [
    ["HIGHER", "Высшее"],
    ["SECONDARY", "Среднее"],
    ["SECONDARY_SPECIAL", "Среднее специальное"],
    ["PHD", "Ученая степень"]
  ],
  region: [
    ["MOSCOW", "Москва"],
    ["SAINT_PETERSBURG", "Санкт-Петербург"],
    ["REGIONAL_CENTER", "Региональный центр"],
    ["OTHER", "Другой регион"]
  ],
  employmentType: [
    ["UNEMPLOYED", "Безработный"],
    ["BUSINESS", "Бизнес"],
    ["PENSIONER", "Пенсионер"],
    ["EMPLOYEE", "Наемный сотрудник"],
    ["SELF_EMPLOYED", "Самозанятый"]
  ],
  cardTypeRequested: [
    ["STANDARD", "Стандартная"],
    ["GOLD", "Золотая"],
    ["PLATINUM", "Платиновая"]
  ]
};

const labels = {
  WIDOWED: "Вдовец / вдова",
  MARRIED: "В браке",
  DIVORCED: "В разводе",
  SINGLE: "Не в браке",
  HIGHER: "Высшее",
  SECONDARY: "Среднее",
  SECONDARY_SPECIAL: "Среднее специальное",
  PHD: "Ученая степень",
  MOSCOW: "Москва",
  SAINT_PETERSBURG: "Санкт-Петербург",
  REGIONAL_CENTER: "Региональный центр",
  OTHER: "Другой регион",
  UNEMPLOYED: "Безработный",
  BUSINESS: "Бизнес",
  PENSIONER: "Пенсионер",
  EMPLOYEE: "Наемный сотрудник",
  SELF_EMPLOYED: "Самозанятый",
  STANDARD: "Стандартная",
  GOLD: "Золотая",
  PLATINUM: "Платиновая",
  IN_PROGRESS: "В обработке",
  COMPLETED: "Завершена",
  FAILED: "Ошибка",
  APPROVED: "Одобрено",
  REJECTED: "Отказ",
  MANUAL_REVIEW: "Ручная проверка"
};

const decisionReasonLabels = {
  "Внутренний скоринг выявил стоп-фактор": "Анкета не прошла один из обязательных скоринговых критериев.",
  "Риск дефолта выше 10%": "Высокая вероятность просрочки по кредиту.",
  "Высокий антифрод риск": "Заявка не прошла проверку безопасности.",
  "Плохая кредитная история": "Кредитная история не соответствует требованиям банка.",
  "Рекомендованный лимит отсутствует": "По текущим данным лимит карты рассчитать не удалось.",
  "Высокий риск дефолта при слабой кредитной истории": "Высокий риск просрочки при недостаточно сильной кредитной истории.",
  "Повышенный риск дефолта при среднем антифрод риске": "Повышенный риск просрочки и дополнительные признаки риска по заявке.",
  "Слабая кредитная история при среднем антифрод риске": "Кредитная история и проверка безопасности требуют дополнительного анализа.",
  "Низкий внутренний скор при дополнительных риск-факторах": "Итоговая оценка анкеты ниже проходного уровня из-за дополнительных факторов риска.",
  "Повышенный риск дефолта": "Повышенная вероятность просрочки.",
  "Средний антифрод риск": "Заявка требует дополнительной проверки безопасности.",
  "Пограничный БКИ скор": "Кредитный рейтинг находится в пограничной зоне.",
  "Низкий внутренний скор": "Итоговая оценка анкеты ниже проходного уровня.",
  "Рекомендованный лимит ниже минимального порога": "Расчетный лимит ниже минимально доступного.",
  "Расчетный лимит ниже минимального порога": "Расчетный лимит ниже минимально доступного.",
  "Много негативных факторов внутреннего скоринга": "По анкете найдено несколько факторов повышенного риска.",
  "Средний БКИ скор при заметном риске дефолта": "Кредитный рейтинг и риск просрочки требуют ручной проверки.",
  "Средний БКИ скор при повышенном антифрод риске": "Кредитный рейтинг и проверка безопасности требуют ручной проверки.",
  "Средний внутренний скор при заметном риске дефолта": "Оценка анкеты и риск просрочки требуют ручной проверки.",
  "Установлен самозапрет на кредитование": "На клиенте установлен самозапрет на кредитование.",
  "Подозрительная заявка": "Заявка не прошла проверку безопасности.",
  "Возраст больше 60": "Возрастная группа повышает риск по скоринговой модели.",
  "Высокая долговая нагрузка": "Высокая текущая долговая нагрузка.",
  "Средняя долговая нагрузка": "Текущая долговая нагрузка выше комфортного уровня.",
  "Пенсионер": "Тип занятости повышает риск по скоринговой модели.",
  "Безработный": "Нет подтвержденной занятости.",
  "Стаж работы меньше 5 лет": "Стаж работы меньше 5 лет.",
  "Малый стаж": "Недостаточный стаж работы.",
  "Отсутствует профильное образование": "Не хватает факторов, повышающих оценку анкеты.",
  "Отсутствие имущества": "Не указано имущество, которое могло бы повысить оценку анкеты.",
  "Отсутствует зарплатный проект": "Нет зарплатного проекта в банке.",
  "Отсутствует депозит": "Нет депозитных продуктов в банке.",
  "Слишком низкий доход": "Доход ниже минимального порога для продукта.",
  "Ненадежный тип занятости": "Тип занятости и уровень дохода не соответствуют требованиям продукта.",
  INTERNAL_SCORE_STOP_FACTOR: "Анкета не прошла один из обязательных скоринговых критериев.",
  STOP_FACTOR: "Анкета не прошла один из обязательных скоринговых критериев.",
  DEFAULT_PROBABILITY_HIGH: "Высокая вероятность просрочки по кредиту.",
  DEFAULT_PROBABILITY_GT_10: "Высокая вероятность просрочки по кредиту.",
  ML_DEFAULT_PROBABILITY_HIGH: "Высокая вероятность просрочки по кредиту.",
  ML_DEFAULT_PROBABILITY_GT_10: "Высокая вероятность просрочки по кредиту.",
  HIGH_DEFAULT_PROBABILITY: "Высокая вероятность просрочки по кредиту.",
  HIGH_ANTIFRAUD_RISK: "Заявка не прошла проверку безопасности.",
  ANTIFRAUD_HIGH_RISK: "Заявка не прошла проверку безопасности.",
  FRAUD_RISK_HIGH: "Заявка не прошла проверку безопасности.",
  SUSPICIOUS_APPLICATION: "Заявка не прошла проверку безопасности.",
  BAD_CREDIT_HISTORY: "Кредитная история не соответствует требованиям банка.",
  POOR_CREDIT_HISTORY: "Кредитная история не соответствует требованиям банка.",
  LOW_BUREAU_SCORE: "Кредитный рейтинг ниже минимального уровня.",
  BUREAU_SCORE_LOW: "Кредитный рейтинг ниже минимального уровня.",
  BORDERLINE_BUREAU_SCORE: "Кредитный рейтинг находится в пограничной зоне.",
  RECOMMENDED_LIMIT_MISSING: "По текущим данным лимит карты рассчитать не удалось.",
  NO_RECOMMENDED_LIMIT: "По текущим данным лимит карты рассчитать не удалось.",
  RECOMMENDED_LIMIT_BELOW_MIN: "Расчетный лимит ниже минимально доступного.",
  LIMIT_BELOW_MIN: "Расчетный лимит ниже минимально доступного.",
  LOW_INTERNAL_SCORE: "Итоговая оценка анкеты ниже проходного уровня.",
  MANY_NEGATIVE_SCORING_FACTORS: "По анкете найдено несколько факторов повышенного риска.",
  SELF_BAN: "На клиенте установлен самозапрет на кредитование.",
  SELF_BANNED: "На клиенте установлен самозапрет на кредитование.",
  LOW_INCOME: "Доход ниже минимального порога для продукта.",
  INCOME_TOO_LOW: "Доход ниже минимального порога для продукта.",
  MONTHLY_INCOME_TOO_LOW: "Доход ниже минимального порога для продукта.",
  TOO_LOW_INCOME: "Доход ниже минимального порога для продукта.",
  HIGH_DTI: "Высокая текущая долговая нагрузка.",
  HIGH_DEBT_LOAD: "Высокая текущая долговая нагрузка.",
  HIGH_DEBT_BURDEN: "Высокая текущая долговая нагрузка.",
  DEBT_LOAD_HIGH: "Высокая текущая долговая нагрузка.",
  MEDIUM_DEBT_LOAD: "Текущая долговая нагрузка выше комфортного уровня.",
  MEDIUM_DEBT_BURDEN: "Текущая долговая нагрузка выше комфортного уровня.",
  AGE_OVER_60: "Возрастная группа повышает риск по скоринговой модели.",
  PENSIONER: "Тип занятости повышает риск по скоринговой модели.",
  UNEMPLOYED: "Нет подтвержденной занятости.",
  EMPLOYMENT_TYPE_UNEMPLOYED: "Нет подтвержденной занятости.",
  UNEMPLOYED_LOW_INCOME: "Тип занятости и уровень дохода не соответствуют требованиям продукта.",
  UNRELIABLE_EMPLOYMENT_TYPE: "Тип занятости и уровень дохода не соответствуют требованиям продукта.",
  SHORT_EMPLOYMENT_LENGTH: "Стаж работы меньше 5 лет.",
  LOW_EMPLOYMENT_LENGTH: "Недостаточный стаж работы.",
  SMALL_EMPLOYMENT_LENGTH: "Недостаточный стаж работы.",
  NO_SPECIALIZED_EDUCATION: "Не хватает факторов, повышающих оценку анкеты.",
  SECONDARY_EDUCATION: "Не хватает факторов, повышающих оценку анкеты.",
  NO_PROPERTY: "Не указано имущество, которое могло бы повысить оценку анкеты.",
  PROPERTY_ABSENT: "Не указано имущество, которое могло бы повысить оценку анкеты.",
  NO_SALARY_PROJECT: "Нет зарплатного проекта в банке.",
  SALARY_PROJECT_ABSENT: "Нет зарплатного проекта в банке.",
  NO_DEPOSIT: "Нет депозитных продуктов в банке.",
  DEPOSIT_ABSENT: "Нет депозитных продуктов в банке."
};

const antifraudFlagLabels = {
  MULTIPLE_APPLICATIONS_30D: "За последние 30 дней было подано несколько заявок.",
  HIGH_APPLICATION_VELOCITY_30D: "За последние 30 дней подано слишком много заявок.",
  REPEAT_APPLICATION_TOO_FAST: "Новая заявка подана слишком скоро после предыдущей.",
  FULL_NAME_CHANGED: "ФИО отличается от данных в предыдущей заявке.",
  EMPLOYMENT_TYPE_CHANGED_TOO_FAST: "Тип занятости изменился слишком быстро после предыдущей заявки.",
  REGION_CHANGED_TOO_FAST: "Регион изменился слишком быстро после предыдущей заявки.",
  INCOME_DOUBLED_30D: "Доход значительно вырос по сравнению с недавней заявкой.",
  INCOME_INCREASED_50_PERCENT_7D: "Доход резко вырос по сравнению с недавней заявкой.",
  PREVIOUS_HIGH_ANTIFRAUD_SCORE: "По прошлым заявкам уже были высокие признаки риска.",
  MULTIPLE_SUSPICIOUS_PREVIOUS_DECISIONS: "По прошлым заявкам несколько раз были признаки риска.",
  THIN_CREDIT_HISTORY_AND_REPEAT_APPLICATIONS: "Короткая кредитная история при повторных заявках.",
  NO_BUREAU_HISTORY_AND_REPEAT_APPLICATIONS: "Нет данных кредитной истории при повторных заявках.",
  RECENT_OVERDUE_AND_REPEAT_APPLICATIONS: "Есть свежие просрочки при повторных заявках.",
  CREDIT_HISTORY_STARTED_BEFORE_18: "Кредитная история выглядит некорректно по возрасту клиента.",
  EMPLOYMENT_STARTED_BEFORE_16: "Стаж работы выглядит некорректно по возрасту клиента.",
  EMPLOYMENT_STARTED_BEFORE_18: "Стаж работы требует проверки по возрасту клиента."
};

const cards = [
  {
    type: "STANDARD",
    title: "Стандартная",
    limit: 250000,
    rate: "от 24.9%",
    tone: "standard",
    details: ["Базовый лимит", "Решение по анкете", "Подходит для первых покупок"]
  },
  {
    type: "GOLD",
    title: "Золотая",
    limit: 500000,
    rate: "от 21.9%",
    tone: "gold",
    details: ["Повышенный лимит", "Более выгодная ставка", "Для активных расходов"]
  },
  {
    type: "PLATINUM",
    title: "Платиновая",
    limit: 1000000,
    rate: "от 18.9%",
    tone: "platinum",
    details: ["Максимальный лимит", "Самые низкие проценты", "Для крупных покупок"]
  }
];

const app = document.querySelector("#app");
let authMode = "login";
let registrationState = {
  step: "phone",
  phone: "",
  verificationId: "",
  callPhone: "",
  callPhonePretty: "",
  expiresAt: "",
  registrationToken: "",
  message: ""
};

const state = {
  applications: [],
  applicationsLoading: false,
  applicationDetail: null,
  detailLoading: false
};

function getAuth() {
  try {
    return JSON.parse(localStorage.getItem(STORAGE_KEY) || "null");
  } catch {
    return null;
  }
}

function setAuth(auth) {
  localStorage.setItem(STORAGE_KEY, JSON.stringify(auth));
}

function clearAuth() {
  localStorage.removeItem(STORAGE_KEY);
}

function isAuthenticated() {
  const auth = getAuth();
  return Boolean(auth?.accessToken);
}

function authHeader() {
  const auth = getAuth();
  if (!auth?.accessToken) return {};
  const tokenType = auth.tokenType || "Bearer";
  return { Authorization: `${tokenType} ${auth.accessToken}` };
}

function getTokenPayload() {
  const token = getAuth()?.accessToken;
  const payload = token?.split(".")[1];
  if (!payload) return null;

  try {
    const normalized = payload.replaceAll("-", "+").replaceAll("_", "/");
    const padded = normalized.padEnd(Math.ceil(normalized.length / 4) * 4, "=");
    const binary = atob(padded);
    const json = decodeURIComponent(
      Array.from(binary, (char) => `%${char.charCodeAt(0).toString(16).padStart(2, "0")}`).join("")
    );

    return JSON.parse(json);
  } catch {
    return null;
  }
}

function isAdmin() {
  return getTokenPayload()?.role === "ADMIN";
}

async function apiFetch(url, options = {}) {
  const response = await fetch(url, {
    ...options,
    headers: {
      "Content-Type": "application/json",
      ...(options.auth === false ? {} : authHeader()),
      ...(options.headers || {})
    }
  });

  const text = await response.text();
  let body = null;

  if (text) {
    try {
      body = JSON.parse(text);
    } catch {
      body = text;
    }
  }

  if (!response.ok) {
    const message = extractError(body, response);
    throw new Error(message);
  }

  return body;
}

function extractError(body, response) {
  if (typeof body === "string" && body.trim()) return body;

  if (body && typeof body === "object") {
    if (body.message) return body.message;
    if (body.detail) return body.detail;
    if (body.error && body.error !== response.statusText) return body.error;
    if (body.title) return body.title;
  }

  return fallbackErrorMessage(response);
}

function fallbackErrorMessage(response) {
  const messages = {
    400: "Некорректный запрос.",
    401: "Нужно войти в систему.",
    403: "Доступ запрещен.",
    404: "Данные не найдены.",
    409: "Запрос конфликтует с текущим состоянием данных.",
    410: "Срок действия операции истек.",
    429: "Слишком много попыток. Попробуйте позже.",
    502: "Backend service is unavailable."
  };

  return messages[response.status] || response.statusText || `Ошибка запроса: ${response.status}`;
}

function navigate(route) {
  window.location.hash = route;
}

function currentRoute() {
  return window.location.hash.replace(/^#/, "") || (isAuthenticated() ? "/home" : "/auth");
}

function money(value) {
  if (value === null || value === undefined || value === "") return "Не указан";
  return new Intl.NumberFormat("ru-RU", {
    style: "currency",
    currency: "RUB",
    maximumFractionDigits: 0
  }).format(value);
}

function dateTime(value) {
  if (!value) return "Не указана";
  return new Intl.DateTimeFormat("ru-RU", {
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit"
  }).format(new Date(value));
}

function telHref(value) {
  const digits = String(value || "").replace(/\D/g, "");
  if (!digits) return "#";
  return `tel:+${digits}`;
}

function escapeHtml(value) {
  return String(value ?? "")
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll('"', "&quot;")
    .replaceAll("'", "&#039;");
}

function formatDecisionReasons(reasons) {
  return formatReasonList(reasons, {
    ...decisionReasonLabels,
    ...antifraudFlagLabels
  }, "Есть дополнительные факторы риска по заявке.");
}

function formatAntifraudFlags(flags) {
  return formatReasonList(flags, antifraudFlagLabels, "Заявка требует дополнительной проверки безопасности.");
}

function formatReasonList(value, dictionary, technicalFallback) {
  const formatted = expandReasonValue(value)
    .map((item) => formatReasonValue(item, dictionary, technicalFallback))
    .filter(Boolean);

  return [...new Set(formatted)];
}

function expandReasonValue(value) {
  if (value === null || value === undefined) return [];

  if (Array.isArray(value)) {
    return value.flatMap(expandReasonValue);
  }

  if (value && typeof value === "object") {
    const preferred = [
      value.message,
      value.description,
      value.reason,
      value.code,
      value.type,
      value.name,
      value.key,
      value.value,
      value.decisionReasons,
      value.decision_reasons,
      value.antifraudFlags,
      value.antifraud_flags
    ].filter((item) => item !== undefined && item !== null);

    return (preferred.length ? preferred : Object.values(value)).flatMap(expandReasonValue);
  }

  const raw = stripOuterQuotes(String(value).trim());
  if (!raw) return [];

  const assignedValue = extractAssignedReasonValue(raw);
  if (assignedValue !== raw) {
    return expandReasonValue(assignedValue);
  }

  const parsed = parseReasonJson(raw);
  if (parsed !== undefined) {
    return expandReasonValue(parsed);
  }

  const pgArray = parsePostgresArray(raw);
  if (pgArray) {
    return pgArray.flatMap(expandReasonValue);
  }

  const split = splitReasonList(raw);
  if (split.length > 1) {
    return split.flatMap(expandReasonValue);
  }

  return [raw];
}

function formatReasonValue(value, dictionary, technicalFallback) {
  const raw = stripOuterQuotes(String(value ?? "").trim());
  if (!raw) return "";

  const label = findReasonLabel(raw, dictionary);
  if (label) return label;

  return isTechnicalReason(raw) ? technicalFallback : raw;
}

function findReasonLabel(reason, dictionary) {
  for (const candidate of reasonCandidates(reason)) {
    const normalized = normalizeReasonKey(candidate);
    const stripped = stripReasonPrefix(normalized);
    const label = dictionary[candidate] ||
      dictionary[normalized] ||
      dictionary[stripped];

    if (label) return label;
  }

  return "";
}

function reasonCandidates(reason) {
  const raw = stripOuterQuotes(String(reason).trim());
  const candidates = [raw];
  const assignedValue = extractAssignedReasonValue(raw);

  if (assignedValue !== raw) {
    candidates.push(assignedValue);
  }

  const pathMatch = raw.match(/(?:^|[.:/\\])([A-Za-z][A-Za-z0-9_]*)(?:\s*)$/);
  if (pathMatch) {
    candidates.push(pathMatch[1]);
  }

  const technicalMatches = raw.match(/[A-Za-z][A-Za-z0-9]*(?:[_:.-][A-Za-z0-9]+)+/g) || [];
  candidates.push(...technicalMatches);

  return [...new Set(candidates.map(stripOuterQuotes).filter(Boolean))];
}

function extractAssignedReasonValue(reason) {
  const match = reason.match(/^(?:[\w.:-]+\s*[:=]\s*)+(.+)$/);
  return match ? stripOuterQuotes(match[1].trim()) : reason;
}

function parseReasonJson(reason) {
  if (!((reason.startsWith("[") && reason.endsWith("]")) ||
        (reason.startsWith("{") && reason.endsWith("}")) ||
        (reason.startsWith("\"") && reason.endsWith("\"")))) {
    return undefined;
  }

  try {
    return JSON.parse(reason);
  } catch {
    return undefined;
  }
}

function parsePostgresArray(reason) {
  if (!(reason.startsWith("{") && reason.endsWith("}"))) return null;

  const body = reason.slice(1, -1).trim();
  if (!body) return [];

  return body
    .split(/,(?=(?:[^"]*"[^"]*")*[^"]*$)/)
    .map((item) => stripOuterQuotes(item.trim()))
    .filter(Boolean);
}

function splitReasonList(reason) {
  if (!/[\n;]/.test(reason)) return [reason];

  return reason
    .split(/[\n;]+/)
    .map((item) => item.trim())
    .filter(Boolean);
}

function stripOuterQuotes(value) {
  let result = String(value ?? "").trim();

  for (let index = 0; index < 3; index += 1) {
    const first = result[0];
    const last = result[result.length - 1];
    const isQuoted = (first === "\"" && last === "\"") ||
      (first === "'" && last === "'") ||
      (first === "`" && last === "`");

    if (!isQuoted) break;
    result = result.slice(1, -1).trim();
  }

  return result;
}

function normalizeReasonKey(reason) {
  return String(reason)
    .trim()
    .replace(/([a-z\d])([A-Z])/g, "$1_$2")
    .replace(/[^A-Za-z0-9]+/g, "_")
    .replace(/^_+|_+$/g, "")
    .toUpperCase();
}

function stripReasonPrefix(key) {
  let result = key;
  let previous = "";

  while (result !== previous) {
    previous = result;
    result = result.replace(
      /^(DECISION|DECISION_REASON|REASON|SCORING|SCORING_REASON|REJECT|REJECT_REASON|MANUAL|MANUAL_REVIEW|MANUAL_REVIEW_REASON|FINAL_DECISION_REASON|ANTIFRAUD|ANTIFRAUD_FLAG|FLAG)_+/,
      ""
    );
  }

  return result;
}

function isTechnicalReason(reason) {
  return /^[A-Z0-9_.:-]+$/.test(reason) ||
    (/^[a-z0-9_.:-]+$/.test(reason) && /[_:.-]/.test(reason));
}

function field(name, label, type = "text", attrs = "") {
  return `
    <label class="field">
      <span>${label}</span>
      <input name="${name}" type="${type}" ${attrs} />
    </label>
  `;
}

function selectField(name, label, options) {
  const optionHtml = options
    .map(([value, text]) => `<option value="${value}">${text}</option>`)
    .join("");

  return `
    <label class="field">
      <span>${label}</span>
      <select name="${name}" required>
        <option value="" selected disabled>Выберите значение</option>
        ${optionHtml}
      </select>
    </label>
  `;
}

function renderShell(pageHtml) {
  const authorized = isAuthenticated();
  const showAdminLink = authorized && isAdmin();
  const route = currentRoute();

  app.innerHTML = `
    <div class="app-shell">
      <header class="topbar">
        <a class="brand" href="#/home" aria-label="Scoring App">
          <span class="brand-mark"></span>
          <span>Scoring App</span>
        </a>
        ${authorized ? `
          <nav class="nav">
            <a href="#/home" data-route="/home">Карты</a>
            <a href="#/apply" data-route="/apply">Анкета</a>
            <a href="#/applications" data-route="/applications">Мои анкеты</a>
          </nav>
          <div class="session">
            ${showAdminLink ? `
              <a class="ghost-button ${route.startsWith("/admin") ? "active" : ""}" href="#/admin">Админка</a>
            ` : ""}
            <button class="ghost-button" id="logoutButton" type="button">Выйти</button>
          </div>
        ` : ""}
      </header>
      <main class="content">
        ${pageHtml}
      </main>
    </div>
  `;

  document.querySelector("#logoutButton")?.addEventListener("click", () => {
    clearAuth();
    navigate("/auth");
    render();
  });

  document.querySelectorAll(".nav a").forEach((link) => {
    if (link.dataset.route === route) {
      link.classList.add("active");
    }
  });
}

function renderAuth() {
  const isLogin = authMode === "login";
  const registrationStep = registrationState.step;

  renderShell(`
    <section class="auth-layout">
      <div class="auth-panel">
        <div class="auth-tabs" role="tablist" aria-label="Авторизация">
          <button class="${isLogin ? "active" : ""}" type="button" data-auth-mode="login">Войти</button>
          <button class="${!isLogin ? "active" : ""}" type="button" data-auth-mode="register">Регистрация</button>
        </div>
        <div id="authMessage" class="message" hidden></div>
        ${isLogin ? loginForm() : registerForm(registrationStep)}
      </div>
      <aside class="auth-side">
        <div>
          <p class="eyebrow">Кредитные карты</p>
          <h1>Подберите лимит и отправьте анкету на скоринг</h1>
        </div>
        <div class="mini-card-stack">
          ${cards.map((card) => `
            <div class="mini-card ${card.tone}">
              <span>${card.title}</span>
              <strong>${money(card.limit)}</strong>
            </div>
          `).join("")}
        </div>
      </aside>
    </section>
  `);

  document.querySelectorAll("[data-auth-mode]").forEach((button) => {
    button.addEventListener("click", () => {
      authMode = button.dataset.authMode;
      render();
    });
  });

  document.querySelector("#loginForm")?.addEventListener("submit", handleLogin);
  document.querySelector("#startRegistrationForm")?.addEventListener("submit", handleStartRegistration);
  document.querySelector("#verifyPhoneForm")?.addEventListener("submit", handleVerifyPhone);
  document.querySelector("#completeRegistrationForm")?.addEventListener("submit", handleCompleteRegistration);
  document.querySelector("#passwordInput")?.addEventListener("input", updatePasswordRules);

  if (registrationStep === "password") {
    updatePasswordRules();
  }
}

function loginForm() {
  return `
    <form id="loginForm" class="form-stack">
      ${field("phone", "Номер телефона", "tel", 'placeholder="+79991234567" pattern="^\\+7\\d{10}$" required autocomplete="tel"')}
      ${field("password", "Пароль", "password", 'required autocomplete="current-password"')}
      <button class="primary-button" type="submit">Войти</button>
    </form>
  `;
}

function registerForm(step) {
  if (step === "phone") {
    return `
      <form id="startRegistrationForm" class="form-stack">
        ${field("phone", "Номер телефона", "tel", 'placeholder="+79991234567" pattern="^\\+7\\d{10}$" required autocomplete="tel"')}
        <button class="primary-button" type="submit">Получить номер для звонка</button>
      </form>
    `;
  }

  if (step === "call") {
    const callPhone = registrationState.callPhonePretty || registrationState.callPhone;

    return `
      <form id="verifyPhoneForm" class="form-stack">
        <div class="inline-status success">${escapeHtml(registrationState.message || "Позвоните на указанный номер для подтверждения телефона")}</div>
        <div class="call-check-card">
          <span>Номер для звонка</span>
          <a href="${telHref(registrationState.callPhone)}">${escapeHtml(callPhone)}</a>
          ${registrationState.expiresAt ? `<small>Действует до ${dateTime(registrationState.expiresAt)}</small>` : ""}
        </div>
        <button class="primary-button" type="submit">Проверить статус звонка</button>
      </form>
    `;
  }

  return `
    <form id="completeRegistrationForm" class="form-stack">
      ${registrationState.message ? `<div class="inline-status success">${escapeHtml(registrationState.message)}</div>` : ""}
      ${field("password", "Пароль", "password", 'id="passwordInput" minlength="8" maxlength="30" required autocomplete="new-password"')}
      <div class="password-rules" id="passwordRules">
        <span data-rule="length">8-30 символов</span>
        <span data-rule="lower">строчная латиница</span>
        <span data-rule="upper">заглавная латиница</span>
        <span data-rule="digit">цифра</span>
        <span data-rule="special">спецсимвол</span>
        <span data-rule="latinOnly">без кириллицы и пробелов</span>
      </div>
      <button class="primary-button" id="completeRegistrationButton" type="submit" disabled>Зарегистрироваться</button>
    </form>
  `;
}

function setAuthMessage(message, type = "error") {
  const node = document.querySelector("#authMessage");
  if (!node) return;

  node.hidden = false;
  node.textContent = message;
  node.className = `message ${type}`;
}

async function handleLogin(event) {
  event.preventDefault();
  const form = event.currentTarget;
  const button = form.querySelector("button");
  const data = Object.fromEntries(new FormData(form));

  await withButtonLoading(button, async () => {
    const auth = await apiFetch("/api/auth/login", {
      method: "POST",
      auth: false,
      body: JSON.stringify(data)
    });
    setAuth(auth);
    navigate("/home");
    render();
  }, setAuthMessage);
}

async function handleStartRegistration(event) {
  event.preventDefault();
  const form = event.currentTarget;
  const button = form.querySelector("button");
  const data = Object.fromEntries(new FormData(form));

  await withButtonLoading(button, async () => {
    const response = await apiFetch("/api/auth/register/start", {
      method: "POST",
      auth: false,
      body: JSON.stringify({ phone: data.phone })
    });
    registrationState = {
      step: "call",
      phone: data.phone,
      verificationId: response.verificationId,
      callPhone: response.callPhone || "",
      callPhonePretty: response.callPhonePretty || "",
      expiresAt: response.expiresAt || "",
      registrationToken: "",
      message: response.message || "Позвоните на указанный номер для подтверждения телефона"
    };
    render();
  }, setAuthMessage);
}

async function handleVerifyPhone(event) {
  event.preventDefault();
  const form = event.currentTarget;
  const button = form.querySelector("button");

  await withButtonLoading(button, async () => {
    const response = await apiFetch("/api/auth/register/verify-phone", {
      method: "POST",
      auth: false,
      body: JSON.stringify({
        verificationId: registrationState.verificationId
      })
    });

    if (response.status !== "VERIFIED") {
      registrationState = {
        ...registrationState,
        message: "Звонок пока не подтвержден. После звонка проверьте статус еще раз."
      };
      render();
      return;
    }

    if (!response.registrationToken) {
      throw new Error("Верификация подтверждена, но registrationToken не получен.");
    }

    registrationState = {
      ...registrationState,
      step: "password",
      registrationToken: response.registrationToken,
      message: "Телефон подтвержден. Теперь задайте пароль."
    };
    render();
  }, setAuthMessage);
}

async function handleCompleteRegistration(event) {
  event.preventDefault();
  const password = document.querySelector("#passwordInput")?.value || "";
  const button = document.querySelector("#completeRegistrationButton");

  if (!isPasswordValid(password)) {
    setAuthMessage("Пароль не соответствует требованиям.");
    return;
  }

  await withButtonLoading(button, async () => {
    const auth = await apiFetch("/api/auth/register/complete", {
      method: "POST",
      auth: false,
      body: JSON.stringify({
        registrationToken: registrationState.registrationToken,
        password
      })
    });
    setAuth(auth);
    registrationState = {
      step: "phone",
      phone: "",
      verificationId: "",
      callPhone: "",
      callPhonePretty: "",
      expiresAt: "",
      registrationToken: "",
      message: ""
    };
    navigate("/home");
    render();
  }, setAuthMessage);
}

function passwordChecks(password) {
  return {
    length: password.length >= 8 && password.length <= 30,
    lower: /[a-z]/.test(password),
    upper: /[A-Z]/.test(password),
    digit: /\d/.test(password),
    special: /[!@#$%^&*()_+\-=[\]{};':"\\|,.<>/?`~]/.test(password),
    latinOnly: /^[\x21-\x7E]*$/.test(password)
  };
}

function isPasswordValid(password) {
  const checks = passwordChecks(password);
  return Object.values(checks).every(Boolean);
}

function updatePasswordRules() {
  const password = document.querySelector("#passwordInput")?.value || "";
  const checks = passwordChecks(password);

  document.querySelectorAll("#passwordRules [data-rule]").forEach((rule) => {
    const isValid = checks[rule.dataset.rule];
    rule.classList.toggle("valid", Boolean(isValid));
  });

  const button = document.querySelector("#completeRegistrationButton");
  if (button) {
    button.disabled = !isPasswordValid(password);
  }
}

async function withButtonLoading(button, action, onError) {
  const originalText = button?.textContent;
  if (button) {
    button.disabled = true;
    button.textContent = "Отправляем...";
  }

  try {
    await action();
  } catch (error) {
    onError(error instanceof Error ? error.message : String(error));
  } finally {
    if (button) {
      button.disabled = false;
      button.textContent = originalText;
    }
  }
}

function renderHome() {
  renderShell(`
    <section class="page-head">
      <div>
        <p class="eyebrow">Линейка карт</p>
        <h1>Кредитные карты с лимитом по результатам скоринга</h1>
      </div>
      <a class="primary-button as-link" href="#/apply">Заполнить анкету</a>
    </section>
    <section class="cards-grid">
      ${cards.map((card) => `
        <article class="product-card ${card.tone}">
          <div class="card-visual">
            <span>${card.title}</span>
            <strong>${card.type}</strong>
          </div>
          <div class="product-card-body">
            <h2>${card.title}</h2>
            <dl>
              <div>
                <dt>Лимит до</dt>
                <dd>${money(card.limit)}</dd>
              </div>
              <div>
                <dt>Проценты</dt>
                <dd>${card.rate}</dd>
              </div>
            </dl>
            <ul>
              ${card.details.map((item) => `<li>${item}</li>`).join("")}
            </ul>
          </div>
        </article>
      `).join("")}
    </section>
  `);
}

function renderApply() {
  renderShell(`
    <section class="page-head compact">
      <div>
        <p class="eyebrow">Анкета</p>
        <h1>Данные для скорингового решения</h1>
      </div>
    </section>
    <section class="workspace">
      <form id="applicationForm" class="application-form">
        <div class="form-section">
          <h2>Клиент</h2>
          <div class="form-grid">
            ${field("fullName", "ФИО", "text", 'maxlength="100" required autocomplete="name"')}
            ${field("age", "Возраст", "number", 'min="1" max="100" required')}
            ${selectField("maritalStatus", "Семейное положение", enums.maritalStatus)}
            ${field("dependents", "Иждивенцы", "number", 'min="0" value="0" required')}
            ${selectField("education", "Образование", enums.education)}
            ${selectField("region", "Регион", enums.region)}
          </div>
        </div>
        <div class="form-section">
          <h2>Работа и доход</h2>
          <div class="form-grid">
            ${selectField("employmentType", "Тип занятости", enums.employmentType)}
            ${field("employmentLength", "Стаж, лет", "number", 'min="0" value="0" required')}
            ${field("monthlyIncome", "Ежемесячный доход", "number", 'min="0" required')}
            ${field("additionalIncome", "Дополнительный доход", "number", 'min="0" value="0" required')}
          </div>
        </div>
        <div class="form-section">
          <h2>Активы и карта</h2>
          <div class="checks-grid">
            ${checkboxField("hasProperty", "Есть недвижимость")}
            ${checkboxField("hasCar", "Есть автомобиль")}
            ${checkboxField("hasSalaryProject", "Зарплатный проект")}
            ${checkboxField("hasDeposit", "Есть вклад")}
          </div>
          <div class="form-grid one">
            ${selectField("cardTypeRequested", "Запрашиваемая карта", enums.cardTypeRequested)}
          </div>
        </div>
        <button class="primary-button submit-wide" id="submitApplicationButton" type="submit">Отправить анкету</button>
      </form>
      <aside class="decision-panel" id="decisionPanel">
        <h2>Решение</h2>
        <p class="muted">Здесь появится результат после обработки анкеты.</p>
      </aside>
    </section>
  `);

  document.querySelector("#applicationForm").addEventListener("submit", handleApplicationSubmit);
}

function checkboxField(name, label) {
  return `
    <label class="check-field">
      <input name="${name}" type="checkbox" />
      <span>${label}</span>
    </label>
  `;
}

async function handleApplicationSubmit(event) {
  event.preventDefault();
  const form = event.currentTarget;
  const button = document.querySelector("#submitApplicationButton");
  const panel = document.querySelector("#decisionPanel");

  if (!form.reportValidity()) return;

  const data = new FormData(form);
  const payload = {
    fullName: data.get("fullName").trim(),
    age: numberValue(data.get("age")),
    maritalStatus: data.get("maritalStatus"),
    dependents: numberValue(data.get("dependents")),
    education: data.get("education"),
    region: data.get("region"),
    employmentType: data.get("employmentType"),
    employmentLength: numberValue(data.get("employmentLength")),
    monthlyIncome: numberValue(data.get("monthlyIncome")),
    additionalIncome: numberValue(data.get("additionalIncome")),
    hasProperty: data.has("hasProperty"),
    hasCar: data.has("hasCar"),
    hasSalaryProject: data.has("hasSalaryProject"),
    hasDeposit: data.has("hasDeposit"),
    cardTypeRequested: data.get("cardTypeRequested")
  };

  button.disabled = true;
  button.textContent = "Ожидаем решение...";
  panel.innerHTML = `
    <h2>Решение</h2>
    <div class="loader-line"></div>
    <p class="muted">Анкета отправлена, скоринг выполняется.</p>
  `;

  try {
    const decision = await apiFetch("/api/scoring", {
      method: "POST",
      body: JSON.stringify(payload)
    });
    panel.innerHTML = decisionResultHtml(decision);
  } catch (error) {
    panel.innerHTML = `
      <h2>Решение</h2>
      <div class="message error">${escapeHtml(error instanceof Error ? error.message : String(error))}</div>
    `;
  } finally {
    button.disabled = false;
    button.textContent = "Отправить анкету";
  }
}

function numberValue(value) {
  const number = Number(value);
  return Number.isFinite(number) ? number : 0;
}

function decisionResultHtml(decision) {
  const reasons = formatDecisionReasons(decision.decisionReasons);
  const finalDecision = decision.finalDecision || "";

  return `
    <h2>Решение</h2>
    <div class="decision-status ${String(finalDecision).toLowerCase()}">
      ${labels[finalDecision] || finalDecision || "Не указано"}
    </div>
    <dl class="detail-list">
      <div>
        <dt>Лимит</dt>
        <dd>${money(decision.approvedLimit)}</dd>
      </div>
    </dl>
    ${decisionReasonsHtml(finalDecision, reasons)}
  `;
}

function decisionReasonsHtml(finalDecision, reasons) {
  if (!reasons.length && finalDecision !== "REJECTED") return "";

  const title = finalDecision === "MANUAL_REVIEW"
    ? "Причины ручной проверки"
    : "Причины отказа";
  const items = reasons.length ? reasons : ["Причины отказа не указаны."];

  return `
    <div class="reasons">
      <h3>${title}</h3>
      <ul>
        ${items.map((reason) => `<li>${escapeHtml(reason)}</li>`).join("")}
      </ul>
    </div>
  `;
}

async function renderApplications() {
  renderShell(`
    <section class="page-head compact">
      <div>
        <p class="eyebrow">История</p>
        <h1>Мои отправленные анкеты</h1>
      </div>
    </section>
    <section class="list-panel" id="applicationsList">
      <div class="loader-line"></div>
    </section>
  `);

  const list = document.querySelector("#applicationsList");

  try {
    state.applications = await apiFetch("/api/applications");
    list.innerHTML = applicationsListHtml(state.applications);
  } catch (error) {
    list.innerHTML = `<div class="message error">${escapeHtml(error instanceof Error ? error.message : String(error))}</div>`;
  }
}

function applicationsListHtml(applications) {
  if (!Array.isArray(applications) || applications.length === 0) {
    return `<p class="muted">Отправленных анкет пока нет.</p>`;
  }

  return `
    <div class="application-list">
      ${applications.map((item) => `
        <a class="application-row" href="#/applications/${item.id}" target="_blank" rel="noopener">
          <time>${dateTime(item.createdAt)}</time>
          <span class="status ${String(item.status || "").toLowerCase()}">${labels[item.status] || item.status}</span>
        </a>
      `).join("")}
    </div>
  `;
}

async function renderApplicationDetail(id) {
  renderShell(`
    <section class="page-head compact">
      <div>
        <p class="eyebrow">Анкета</p>
        <h1>Полная информация</h1>
      </div>
    </section>
    <section class="detail-panel" id="applicationDetail">
      <div class="loader-line"></div>
    </section>
  `);

  const detail = document.querySelector("#applicationDetail");

  try {
    const response = await apiFetch(`/api/applications/${id}`);
    detail.innerHTML = applicationDetailHtml(response);
  } catch (error) {
    detail.innerHTML = `<div class="message error">${escapeHtml(error instanceof Error ? error.message : String(error))}</div>`;
  }
}

function applicationDetailHtml(response) {
  const application = response.applicationResponse || {};
  const decision = response.decisionResult || {};

  return `
    <div class="detail-grid">
      <article>
        <h2>Анкета</h2>
        <dl class="detail-list">
          ${detailItem("Дата", dateTime(application.createdAt))}
          ${detailItem("Статус", labels[application.status] || application.status)}
          ${detailItem("ФИО", application.fullName)}
          ${detailItem("Возраст", application.age)}
          ${detailItem("Семейное положение", labels[application.maritalStatus] || application.maritalStatus)}
          ${detailItem("Иждивенцы", application.dependents)}
          ${detailItem("Образование", labels[application.education] || application.education)}
          ${detailItem("Регион", labels[application.region] || application.region)}
          ${detailItem("Тип занятости", labels[application.employmentType] || application.employmentType)}
          ${detailItem("Стаж", `${application.employmentLength ?? 0} лет`)}
          ${detailItem("Ежемесячный доход", money(application.monthlyIncome))}
          ${detailItem("Дополнительный доход", money(application.additionalIncome))}
          ${detailItem("Недвижимость", boolLabel(application.hasProperty))}
          ${detailItem("Автомобиль", boolLabel(application.hasCar))}
          ${detailItem("Зарплатный проект", boolLabel(application.hasSalaryProject))}
          ${detailItem("Вклад", boolLabel(application.hasDeposit))}
          ${detailItem("Карта", labels[application.cardTypeRequested] || application.cardTypeRequested)}
        </dl>
      </article>
      <article>
        ${decisionResultHtml(decision)}
      </article>
    </div>
  `;
}

function detailItem(label, value) {
  return `
    <div>
      <dt>${label}</dt>
      <dd>${escapeHtml(value ?? "Не указано")}</dd>
    </div>
  `;
}

function boolLabel(value) {
  return value ? "Да" : "Нет";
}

const adminRoutes = createAdminRoutes({
  renderShell,
  apiFetch,
  escapeHtml,
  dateTime,
  money,
  labels,
  enums,
  detailItem,
  boolLabel,
  numberValue,
  formatDecisionReasons,
  formatAntifraudFlags
});

function requireAuth(route) {
  if (!isAuthenticated()) {
    navigate("/auth");
    return false;
  }
  return true;
}

function renderAdminForbidden() {
  renderShell(`
    <section class="page-head compact">
      <div>
        <p class="eyebrow">Админка</p>
        <h1>Доступ только для сотрудников</h1>
      </div>
    </section>
    <section class="list-panel">
      <div class="message error">У текущего пользователя нет роли ADMIN.</div>
    </section>
  `);
}

function render() {
  const route = currentRoute();

  if (route === "/auth") {
    renderAuth();
    return;
  }

  if (!requireAuth(route)) return;

  if (route.startsWith("/admin") && !isAdmin()) {
    renderAdminForbidden();
    return;
  }

  if (route === "/home") {
    renderHome();
    return;
  }

  if (route === "/apply") {
    renderApply();
    return;
  }

  if (route === "/applications") {
    renderApplications();
    return;
  }

  if (route === "/admin") {
    adminRoutes.renderAdminReview();
    return;
  }

  const applicationMatch = route.match(/^\/applications\/([0-9a-fA-F-]+)$/);
  if (applicationMatch) {
    renderApplicationDetail(applicationMatch[1]);
    return;
  }

  const adminApplicationMatch = route.match(/^\/admin\/applications\/([0-9a-fA-F-]+)$/);
  if (adminApplicationMatch) {
    adminRoutes.renderAdminApplicationDetail(adminApplicationMatch[1], { readonly: false });
    return;
  }

  const adminUserApplicationsMatch = route.match(/^\/admin\/users\/([0-9a-fA-F-]+)\/applications$/);
  if (adminUserApplicationsMatch) {
    adminRoutes.renderAdminUserApplications(adminUserApplicationsMatch[1]);
    return;
  }

  const adminUserApplicationMatch = route.match(/^\/admin\/users\/([0-9a-fA-F-]+)\/applications\/([0-9a-fA-F-]+)$/);
  if (adminUserApplicationMatch) {
    adminRoutes.renderAdminApplicationDetail(adminUserApplicationMatch[2], {
      readonly: true,
      userId: adminUserApplicationMatch[1]
    });
    return;
  }

  navigate(isAuthenticated() ? "/home" : "/auth");
}

window.addEventListener("hashchange", render);
render();
