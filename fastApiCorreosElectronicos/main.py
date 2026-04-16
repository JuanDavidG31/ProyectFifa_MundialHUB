import os
import base64
import resend
from fastapi import FastAPI, BackgroundTasks
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel, EmailStr
from dotenv import load_dotenv

load_dotenv()

# --- CONFIGURACIÓN DE RESEND ---
resend.api_key = os.getenv("RESEND_API_KEY")

app = FastAPI(title="MundialHub Email API")

# --- CORS ---
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=False,
    allow_methods=["*"],
    allow_headers=["*"],
)

# --- MODELOS DE DATOS ---
class VerifyEmailRequest(BaseModel):
    email: EmailStr
    codigo: str

class PasswordResetRequest(BaseModel):
    email: EmailStr

class TicketRequest(BaseModel):
    email: EmailStr
    match_name: str
    stadium: str
    date: str
    ticket_uuid: str
    qr_base64: str

# --- FUNCIONES DE ENVÍO CON RESEND ---
def enviar_correo_base(to_email: str, subject: str, html_content: str):
    try:
        # NOTA: En la capa gratuita de Resend, el remitente SIEMPRE debe ser onboarding@resend.dev
        # y el correo de destino (to_email) debe ser el que verificaste en tu cuenta de Resend.
        params = {
            "from": "onboarding@resend.dev", 
            "to": to_email,
            "subject": subject,
            "html": html_content
        }
        resend.Emails.send(params)
        print("DEBUG: Correo base enviado exitosamente vía Resend")
    except Exception as e:
        print(f"Error enviando correo con Resend: {e}")

def enviar_correo_ticket_qr(req: TicketRequest):
    try:
        # 1. Limpiar el Base64 por si trae la cabecera 'data:image/png;base64,'
        qr_data = req.qr_base64
        if "," in qr_data:
            qr_data = qr_data.split(",")[1]
            
        # Convertimos el Base64 a una lista de bytes (formato que exige la librería de Resend)
        image_bytes = base64.b64decode(qr_data.replace(" ", ""))
        
        # HTML del ticket con la imagen incrustada directamente por Base64
        html_body = f"""
        <div style='font-family: Arial, sans-serif; background-color: #0b0f19; color: #ffffff; padding: 30px; text-align: center; border-radius: 15px;'>
            <h2 style='color: #ec4899; margin-bottom: 5px;'>¡Compra Exitosa!</h2>
            <p style='color: #94a3b8; font-size: 16px;'>Has adquirido una entrada oficial.</p>
            <div style='background-color: rgba(255,255,255,0.05); padding: 20px; border-radius: 10px; margin: 20px auto; max-width: 400px; border: 1px solid rgba(255,255,255,0.1);'>
                <h3 style='margin: 0 0 10px 0;'>{req.match_name}</h3>
                <p style='margin: 5px 0; color: #06b6d4;'>📍 {req.stadium}</p>
                <p style='margin: 5px 0; color: #94a3b8;'>📅 {req.date}</p>
            </div>
            <p>Presenta este código QR en la entrada del estadio:</p>
            <img src='data:image/png;base64,{qr_data}' alt='Código QR' style='width: 250px; height: 250px; border-radius: 15px; border: 3px solid #ec4899; margin-top: 15px;' />
            <p style='margin-top: 30px; font-size: 12px; color: #64748b;'>Ticket ID: {req.ticket_uuid}</p>
        </div>
        """
        
        params = {
            "from": "onboarding@resend.dev",
            "to": req.email,
            "subject": f"🎟️ ¡Tu entrada confirmada para {req.match_name}!",
            "html": html_body,
            # Para mayor seguridad, además de mostrarlo en el HTML, lo enviamos como archivo adjunto
            "attachments": [
                {
                    "filename": f"ticket_qr_{req.ticket_uuid}.png",
                    "content": list(image_bytes) 
                }
            ]
        }
        resend.Emails.send(params)
        print("DEBUG: Ticket con QR enviado exitosamente vía Resend")
    except Exception as e:
        print(f"Error enviando ticket con Resend: {e}")

# --- ENDPOINTS ---

@app.post("/auth/sendEmailVerifyCode")
async def send_email_verify_code(req: VerifyEmailRequest, background_tasks: BackgroundTasks):
    html_body = f"""
    <div style='font-family: Arial, sans-serif; background-color: #0b0f19; color: #ffffff; padding: 30px; text-align: center; border-radius: 15px; max-width: 500px; margin: 0 auto;'>
        <h2 style='color: #ec4899; margin-bottom: 5px;'>¡Verifica tu cuenta!</h2>
        <p style='color: #94a3b8; font-size: 16px;'>Usa el siguiente código de seguridad para acceder a tu cuenta en MundialHub:</p>
        <div style='background-color: rgba(255,255,255,0.05); padding: 20px; border-radius: 10px; margin: 25px auto; max-width: 200px; border: 1px solid rgba(255,255,255,0.1); font-size: 32px; font-weight: bold; letter-spacing: 5px; color: #06b6d4;'>
            {req.codigo}
        </div>
        <p style='margin-top: 30px; font-size: 12px; color: #64748b;'>Si no solicitaste este código, por favor ignora este correo. Tu cuenta está segura.</p>
    </div>
    """
    background_tasks.add_task(enviar_correo_base, req.email, "🔒 Bienvenido a MundialHub - Código de Verificación", html_body)
    return {"message": "Correo de verificación enviado", "success": True}

@app.post("/auth/sendEmailPasswordUpdate")
async def send_email_password_update(req: PasswordResetRequest, background_tasks: BackgroundTasks):
    html_body = f"""
    <div style='font-family: Arial, sans-serif; background-color: #0b0f19; color: #ffffff; padding: 30px; text-align: center; border-radius: 15px; max-width: 500px; margin: 0 auto;'>
        <h2 style='color: #06b6d4; margin-bottom: 5px;'>Contraseña Actualizada</h2>
        <p style='color: #94a3b8; font-size: 16px;'>Hola,</p>
        <p style='color: #94a3b8; font-size: 16px;'>Te informamos que la contraseña de tu cuenta en MundialHub ha sido modificada exitosamente.</p>
        <div style='background-color: rgba(255,255,255,0.05); padding: 20px; border-radius: 10px; margin: 25px auto; max-width: 300px; border: 1px solid rgba(255,255,255,0.1);'>
            <p style='margin: 0; color: #ec4899; font-weight: bold;'>Si fuiste tú, no tienes que hacer nada más.</p>
        </div>
        <p style='margin-top: 30px; font-size: 12px; color: #64748b;'>Si no realizaste este cambio, por favor contacta a soporte inmediatamente.</p>
    </div>
    """
    background_tasks.add_task(enviar_correo_base, req.email, "🔑 MundialHub - Actualización de Contraseña", html_body)
    return {"message": "Correo de actualización de contraseña enviado", "success": True}

@app.post("/email/sendTicketWithQR")
async def send_ticket_with_qr(req: TicketRequest, background_tasks: BackgroundTasks):
    background_tasks.add_task(enviar_correo_ticket_qr, req)
    return {"message": "Ticket enviado exitosamente", "success": True}