package com.AlanTitor.socks.Repository;

import com.AlanTitor.socks.Model.SocksEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SocksRepo extends JpaRepository<SocksEntity, Long> {
    Optional<SocksEntity> findByColorAndPercentCotton(String color, Double percentCotton);

    List<SocksEntity> findByColorAndPercentCottonGreaterThan(@Param("color")String color, @Param("percentCotton")Double percentCotton);
    List<SocksEntity> findByColorAndPercentCottonLessThan(@Param("color")String color, @Param("percentCotton")Double percentCotton);
    List<SocksEntity> findByColorAndPercentCottonEquals(@Param("color")String color, @Param("percentCotton")Double percentCotton);
    List<SocksEntity> findByColorAndPercentCottonBetween(@Param("color")String color, @Param("minPercentCotton")Double minPercentCotton, @Param("maxPercentCotton")Double maxPercentCotton);
}
