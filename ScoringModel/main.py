from fastapi import FastAPI, HTTPException

from schemas import ScoringRequest, MlScoringResponse
from predictor import ModelPredictor

app = FastAPI(title="ML Scoring Service", version="1.0.0")
predictor = ModelPredictor()


@app.get("/health")
def health():
    return {"status": "ok"}


@app.post("/internal/ml/predict", response_model=MlScoringResponse)
def predict(request: ScoringRequest):
    try:
        return predictor.predict(request.model_dump())
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Prediction failed: {str(e)}")