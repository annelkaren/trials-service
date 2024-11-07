package mx.gob.pjpuebla.trials.core.menu;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Integer> {

    @Query(value = "SELECT * FROM TBL_MENUS WHERE S_ROL ~* :roles", nativeQuery = true)
    List<Menu> findMenus(String roles);
}
