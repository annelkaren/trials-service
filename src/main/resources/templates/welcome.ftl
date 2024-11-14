<!DOCTYPE html>
<html>
<head>
    <title>¡Bienvenido a nuestro portal!</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            color: #333;
            background-color: #f4f4f4;
            margin: 0;
            padding: 20px;
        }

        .container {
            background-color: #ffffff;
            padding: 20px;
            border-radius: 5px;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
            max-width: 600px;
            margin: auto;
        }

        h1 {
            color: #007BFF;
            font-size: 24px;
            border-bottom: 2px solid #007BFF;
            padding-bottom: 10px;
        }

        p {
            font-size: 16px;
            line-height: 1.5;
            margin: 15px 0;
        }

        .info-section {
            margin-top: 20px;
            padding: 10px;
            background-color: #f9f9f9;
            border-left: 4px solid #007BFF;
            border-radius: 4px;
        }

        .info-section p {
            margin: 5px 0;
        }

        .footer {
            font-size: 12px;
            color: #888;
            margin-top: 20px;
            border-top: 1px solid #ddd;
            padding-top: 10px;
            text-align: justify;
        }

        .footer p {
            margin: 5px 0;
            line-height: 1.4;
        }

        .footer strong {
            font-weight: bold;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="info-section">
            <p>Hola <strong>${name}</strong> <br/><br/>
                Le damos la bienvenida al Sistema Electrónico de Control y Gestión Judicial.
                Se ha creado una nueva cuenta con las siguientes credenciales:
            </p>
        </div>

        <p>Usuario: <strong>${username}</strong> </p>
        <p>Contraseña: <strong>${password}</strong></p>

        <div class="footer">
            <p>Este correo electrónico, así como sus anexos, está destinado únicamente al destinatario(s) aquí nombrado(s) y puede contener información privilegiada y/o confidencial. Si usted no es su destinatario, se le notifica que cualquier divulgación, distribución o copia está estrictamente prohibida. Si ha recibido este correo electrónico por error, favor de notificar al emisor respondiendo a este mensaje y eliminar el presente permanentemente.</p>
        </div>
    </div>
</body>
</html>
