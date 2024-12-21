package com.AlanTitor.socks.Service;

import com.AlanTitor.socks.Model.SocksEntity;
import com.AlanTitor.socks.DTO.Socks;
import com.AlanTitor.socks.Repository.SocksRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.List;

@Service
public class SocksService {
    @Autowired
    private SocksRepo socksRepo;

    //Добавляет новые носки
    public void registerIncome(Socks socks){
        if(socks.getColor() == null || socks.getPercentCotton() == null || socks.getAmount() == null){
            throw new IllegalArgumentException("Color, percent cotton and amount cannot be null");
        }
        Optional<SocksEntity> existingSocks = socksRepo.findByColorAndPercentCotton(socks.getColor(), socks.getPercentCotton());

        if(existingSocks.isPresent()){
            SocksEntity socksEntity = existingSocks.get();
            socksEntity.setAmount(socksEntity.getAmount() + socks.getAmount());
            socksRepo.save(socksEntity);
        }else{
            SocksEntity newSocks = new SocksEntity(null, socks.getColor(), socks.getPercentCotton(), socks.getAmount());
            socksRepo.save(newSocks);
        }
    }
    //Уменьшает носки
    public void registerOutcome(Socks socks){
        if(socks.getColor() == null || socks.getPercentCotton() == null || socks.getAmount() == null){
            throw new IllegalArgumentException("Color, percent cotton and amount cannot be null");
        }
        Optional<SocksEntity> existingSocks = socksRepo.findByColorAndPercentCotton(socks.getColor(), socks.getPercentCotton());

        if(existingSocks.isPresent()){
            SocksEntity socksEntity = existingSocks.get();
            int newAmount = socksEntity.getAmount() - socks.getAmount();
            if(newAmount < 0){
                throw new IllegalArgumentException("Not enough socks!");
            }
            socksEntity.setAmount(newAmount);
            socksRepo.save(socksEntity);
        }else{
            throw new IllegalArgumentException("Socs not found");
        }
    }

    //Получаем общее колличество с условием
    public Integer getTotal(String color, String operation, Double percentCotton, Double minPercentCotton, Double maxPercentCotton){
        List<SocksEntity> socksList;

        return switch (operation) {
            case "moreThan" -> {
                socksList = socksRepo.findByColorAndPercentCottonGreaterThan(color, percentCotton);
                yield socksList.stream().mapToInt(SocksEntity::getAmount).sum();
            }
            case "lessThan" -> {
                socksList = socksRepo.findByColorAndPercentCottonLessThan(color, percentCotton);
                yield socksList.stream().mapToInt(SocksEntity::getAmount).sum();
            }
            case "equal" -> {
                socksList = socksRepo.findByColorAndPercentCottonEquals(color, percentCotton);
                yield socksList.stream().mapToInt(SocksEntity::getAmount).sum();
            }
            case "between" -> {
                socksList = socksRepo.findByColorAndPercentCottonBetween(color, minPercentCotton, maxPercentCotton);
                yield socksList.stream().mapToInt(SocksEntity::getAmount).sum();
            }
            default -> throw new IllegalArgumentException("Invalid operation" + operation);
        };
    }

    public void updateSocks(Long id, Socks socks){
        //Проверка на пустоту поля
        if(socks.getColor().isEmpty() || socks.getPercentCotton().isNaN() || socks.getAmount() == null){
            throw new IllegalArgumentException("Params can't be empty!");
        }
        //Проверка меньше нуля значение
        if(socks.getPercentCotton() < 0 || socks.getAmount() < 0){
            throw new IllegalArgumentException("Params can't less than zero!");
        }
        //В БД значение ID не должны быть меньше нуля!
        if(id <= 0){
            throw new IllegalArgumentException("Id can't be zero or less!");
        }

        SocksEntity socksEntity = socksRepo.findById(id).orElseThrow(()-> new IllegalArgumentException("Socks with id: " + id + " not found!"));

        socksEntity.setColor(socks.getColor());
        socksEntity.setPercentCotton(socks.getPercentCotton());
        socksEntity.setAmount(socks.getAmount());

        socksRepo.save(socksEntity);
    }

    public void saveExcel(List<Socks> list){
        for(Socks socks : list){

            Optional<SocksEntity> existingSocks = socksRepo.findByColorAndPercentCotton(socks.getColor(), socks.getPercentCotton());

            if(existingSocks.isPresent()){
                SocksEntity socksEntity = existingSocks.get();
                if(socks.getAmount() <  socksEntity.getAmount()){
                    socksEntity.setAmount(socksEntity.getAmount() - socks.getAmount());
                    socksRepo.save(socksEntity);
                }else{
                    socksEntity.setAmount(socksEntity.getAmount() + socks.getAmount());
                    socksRepo.save(socksEntity);
                }
            }else{
                SocksEntity newSocks = new SocksEntity(null, socks.getColor(), socks.getPercentCotton(), socks.getAmount());
                socksRepo.save(newSocks);
            }
        }
    }
}
