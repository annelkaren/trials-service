ALTER TABLE trials.tbl_email_logs 
  ALTER COLUMN s_provider_message_id TYPE varchar,
  ALTER COLUMN s_to_email TYPE varchar,
  ALTER COLUMN s_to_name TYPE varchar,
  ALTER COLUMN s_subject TYPE varchar,
  ALTER COLUMN s_smtp_answer_code_explain TYPE varchar,
  ALTER COLUMN s_error_envio_detalle TYPE varchar,
  ALTER COLUMN s_provider TYPE varchar;