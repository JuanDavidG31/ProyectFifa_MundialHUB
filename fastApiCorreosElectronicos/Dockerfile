# Usamos una imagen ligera de Python 3.11 para que el despliegue sea rápido
FROM python:3.11-slim

# Establecemos el directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiamos el archivo de dependencias (requirements.txt)
COPY requirements.txt .

# Instalamos las librerías necesarias sin guardar caché para ahorrar espacio
RUN pip install --no-cache-dir -r requirements.txt

# Copiamos todo el código de tu proyecto (main.py, etc.) al contenedor
COPY . .

# Exponemos el puerto 8080, que es el que usa Render por defecto
EXPOSE 8080

# Comando para arrancar la API en modo producción
# 'fastapi run' detecta automáticamente tu app y la sirve en el puerto indicado
CMD ["fastapi", "run", "main.py", "--port", "8080"]