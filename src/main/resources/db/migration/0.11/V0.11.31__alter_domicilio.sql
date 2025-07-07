-- se aumenta el tamaño de la latitud y longitud para poder almacenar completo el dato proveniende de google.
ALTER TABLE trials.tbl_domicilios ALTER COLUMN s_latitud TYPE varchar(20) USING s_latitud::varchar(20);
ALTER TABLE trials.tbl_domicilios ALTER COLUMN s_longitud TYPE varchar(20) USING s_longitud::varchar(20);
