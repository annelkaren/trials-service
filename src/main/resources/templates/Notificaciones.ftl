<!DOCTYPE html>
<html>
<head>
    <title>Notificación</title>
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
            <p><strong> Estimado/a ${nombreParticipante}: </strong></p>

            <p>Te informamos que tienes una <strong>notificación</strong> en el Sistema Electrónico de Control y Gestión Judicial relacionado con el ${toca} perteneciente al la sala ${nombreSala}  </p>
           
        </div>

        <div class="footer">
            <p><strong>Mensaje de privacidad: </strong></p>
            <p>La información transmitida en el presente mensaje tiene la intención de estar dirigida únicamente a la persona o entidad que se refiere y puede contener información privilegiada y/o confidencial. Cualquier revisión, retransmisión, diseminación o cualquier uso impropio o relacionado con dicha información por persona alguna distinta a la que fue dirigido este mensaje queda estrictamente prohibida. Si Usted ha recibido este mensaje o sus anexos por error, favor de contactar al remitente y elimine el material de cualquier computadora.</p>
        </div>
    </div>
</body>
</html>
