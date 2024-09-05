create trigger trg_generar_secuencia after
insert
    on
    trials.tbl_juzgados for each row execute function trials.fun_generar_secuencia();


create trigger trg_eliminar_secuencia after
delete
    on
    trials.tbl_juzgados for each row execute function trials.fun_eliminar_secuencia();