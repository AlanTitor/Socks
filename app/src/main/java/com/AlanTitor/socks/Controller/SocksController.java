package com.AlanTitor.socks.Controller;

import com.AlanTitor.socks.DTO.Socks;
import com.AlanTitor.socks.ExcelReader.Excel;
import com.AlanTitor.socks.Service.SocksService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;


@RestController
@RequestMapping("api/socks")
@Tag(name = "Socks Controller", description = "Endpoints for socks operations")
public class SocksController {
    @Autowired
    private SocksService socksService;

    public static final Logger logger = LoggerFactory.getLogger(SocksController.class);

    //Добавление новых носков или добавление к существующим
    @CrossOrigin(origins = "*", methods = {RequestMethod.POST})
    @PostMapping("/income")
    @Operation(summary = "Add new socks", description = "Adding new different socks to DB")
    public ResponseEntity<String> incomeSocks(@RequestBody Socks socks){
        logger.info("Received request to register income for socks: color={}, percentCotton={}, amount={}", socks.getColor(), socks.getPercentCotton(), socks.getAmount());

        if(socks.getColor() == null || socks.getPercentCotton() == null || socks.getAmount() == null){
            logger.warn("Invalid request: color, percent cotton, or amount is null");
            return ResponseEntity.badRequest().body("Color, percent cotton and amount cannot be null");
        }
        if(socks.getPercentCotton() < 0 || socks.getAmount() < 0){
            logger.warn("Invalid request: amount is less than zero");
            return ResponseEntity.badRequest().body("Percent and amount cannot be less than zero");
        }
        try{
            socksService.registerIncome(socks);
            logger.info("Income registered successfully for socks: color={}, percentCotton={}, amount={}", socks.getColor(), socks.getPercentCotton(), socks.getAmount());
            return ResponseEntity.ok("Income is OK");
        }catch(IllegalArgumentException e){
            logger.error("Failed to register income: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Failed to register income " + e.getMessage());
        }
    }
    //Удалени носков
    @CrossOrigin(origins = "*", methods = {RequestMethod.POST})
    @PostMapping("/outcome")
    @Operation(summary = "Remove socks", description = "Decreasing existing socks from DB")
    public ResponseEntity<String> outcomeSocks(@RequestBody Socks socks){
        logger.info("Received request to register outcome for socks: color={}, percentCotton={}, amount={}", socks.getColor(), socks.getPercentCotton(), socks.getAmount());

        if(socks.getColor() == null || socks.getPercentCotton() == null || socks.getAmount() == null){
            logger.warn("Invalid request: color, percent cotton, or amount is null");
            return ResponseEntity.badRequest().body("Color, percent cotton and amount cannot be null");
        }
        try{
            socksService.registerOutcome(socks);
            logger.info("Outcome registered successfully for socks: color={}, percentCotton={}, amount={}", socks.getColor(), socks.getPercentCotton(), socks.getAmount());
            return ResponseEntity.ok("Outcome is OK");
        }catch(IllegalArgumentException e){
            logger.error("Failed to register outcome: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Failed to register outcome: " + e.getMessage());
        }
    }
    //Получение колличества носков по условию
    @CrossOrigin(origins = "*", methods = {RequestMethod.GET})
    @GetMapping
    @Operation(summary = "Get amount of socks", description = "To get amount of socks from DB by request of user with different params")
    public ResponseEntity<String> filteredSocks(@RequestParam String color, @RequestParam String operation, @RequestParam(required = false) Double percentCotton, @RequestParam(required = false)Double minPercentCotton, @RequestParam(required = false)Double maxPercentCotton){
        logger.info("Received request to get filtered socks: color={}, operation={}, percentCotton={}", color, operation, percentCotton);
        try{
            Integer total = socksService.getTotal(color, operation, percentCotton, minPercentCotton, maxPercentCotton);
            if(total == null){
                total = 0;
            }
            logger.info("Filtered socks result: total={}", total);
            return ResponseEntity.ok("Total " + total);
        }catch(IllegalArgumentException e){
            logger.error("Failed to get filtered socks: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Error");
        }
    }
    //Ищменение носков по id
    @CrossOrigin(origins = "*", methods = {RequestMethod.PUT})
    @PutMapping("/{id}")
    @Operation(summary = "Change socks", description = "To change socks by existing correct id")
    public ResponseEntity<String> update(@PathVariable Long id, @RequestBody Socks socks){
        logger.info("Received request to update socks with id={}: color={}, percentCotton={}, amount={}", id, socks.getColor(), socks.getPercentCotton(), socks.getAmount());
        try{
            socksService.updateSocks(id, socks);
            logger.info("Socks with id={} updated successfully", id);
            return ResponseEntity.ok("Socks with id: " + id + " have been changed!");
        }catch(IllegalArgumentException e){
            logger.error("Failed to update socks with id={}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body("Couldn't change socks with id: " + id);
        }
    }
    //Загрузка с Excel
    @CrossOrigin(origins = "*", methods = {RequestMethod.POST})
    @PostMapping("/batch")
    @Operation(summary = "Load from Excel", description = "To load socks table to DB from excel file")
    public ResponseEntity<String> uploadExcel(@RequestParam("file") MultipartFile file) throws IOException {
        logger.info("Received request to upload Excel file");
        if(file.isEmpty()) return ResponseEntity.badRequest().body("File is empty!");
        if (!Objects.equals(file.getContentType(), "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) return ResponseEntity.badRequest().body("Неверный формат файла. Ожидается .xlsx.");

        try{
            List<Socks> list = Excel.readSocksFromExcel(file.getInputStream());
            socksService.saveExcel(list);
            return ResponseEntity.ok("File added successfully!");
        } catch (InvalidFormatException e) {
            logger.error("Failed to process Excel file: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Failed to load file!");
        }
    }
}
