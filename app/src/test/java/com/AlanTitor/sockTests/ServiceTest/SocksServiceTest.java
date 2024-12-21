package com.AlanTitor.sockTests.ServiceTest;

import com.AlanTitor.socks.DTO.Socks;
import com.AlanTitor.socks.Model.SocksEntity;
import com.AlanTitor.socks.Repository.SocksRepo;
import com.AlanTitor.socks.Service.SocksService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SocksServiceTest {

    @Mock
    private SocksRepo socksRepo;

    @InjectMocks
    private SocksService socksService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRegisterIncome_NewSocks() {
        Socks socks = new Socks(null, "red", 50.0, 10);
        when(socksRepo.findByColorAndPercentCotton("red", 50.0)).thenReturn(Optional.empty());

        socksService.registerIncome(socks);

        verify(socksRepo, times(1)).save(any(SocksEntity.class));
    }

    @Test
    public void testRegisterIncome_ExistingSocks() {
        Socks socks = new Socks(null, "red", 50.0, 10);
        SocksEntity existingSocks = new SocksEntity(1L, "red", 50.0, 10);
        when(socksRepo.findByColorAndPercentCotton("red", 50.0)).thenReturn(Optional.of(existingSocks));

        socksService.registerIncome(socks);

        verify(socksRepo, times(1)).save(any(SocksEntity.class));
        assertEquals(20, existingSocks.getAmount());
    }

    @Test
    public void testRegisterIncome_NullInput() {
        Socks socks = new Socks(null, null, null, null);

        assertThrows(IllegalArgumentException.class, ()->socksService.registerIncome(socks));
    }

    @Test
    public void testRegisterOutcome_NotEnoughSocks() {
        Socks socks = new Socks(null, "red", 50.0, 10);
        SocksEntity existingSocks = new SocksEntity(1L, "red", 50.0, 5);
        when(socksRepo.findByColorAndPercentCotton("red", 50.0)).thenReturn(Optional.of(existingSocks));


        assertThrows(IllegalArgumentException.class, () -> socksService.registerOutcome(socks));
    }

    @Test
    public void testRegisterOutcome_EnoughSocks() {
        Socks socks = new Socks(null, "red", 50.0, 5);
        SocksEntity existingSocks = new SocksEntity(1L, "red", 50.0, 10);
        when(socksRepo.findByColorAndPercentCotton("red", 50.0)).thenReturn(Optional.of(existingSocks));

        ArgumentCaptor<SocksEntity> captor = ArgumentCaptor.forClass(SocksEntity.class);

        socksService.registerOutcome(socks);

        verify(socksRepo, times(1)).save(captor.capture());
        SocksEntity savedSocks = captor.getValue();
        assertEquals(socks.getAmount(), existingSocks.getAmount());
    }
    @Test
    public void testGetTotal_MoreThan() {
        when(socksRepo.findByColorAndPercentCottonGreaterThan("red", 50.0)).thenReturn(List.of(new SocksEntity(1L, "red", 60.0, 10)));

        Integer total = socksService.getTotal("red", "moreThan", 50.0, null,null);

        assertEquals(10, total);
    }
    @Test
    public void testGetTotal_lessThan() {
        when(socksRepo.findByColorAndPercentCottonLessThan("red", 60.0)).thenReturn(List.of(new SocksEntity(1L, "red", 20.0, 10)));

        Integer total = socksService.getTotal("red", "lessThan", 60.0, null,null);

        assertEquals(10, total);
    }
    @Test
    public void testGetTotal_equal() {
        when(socksRepo.findByColorAndPercentCottonEquals("red", 50.0)).thenReturn(List.of(new SocksEntity(1L, "red", 50.0, 10)));

        Integer total = socksService.getTotal("red", "equal", 50.0, null,null);

        assertEquals(10, total);
    }
    @Test
    public void testGetTotal_between() {
        when(socksRepo.findByColorAndPercentCottonBetween("red", 50.0, 80.0)).thenReturn(List.of(new SocksEntity(1L, "red", 60.0, 10)));

        Integer total = socksService.getTotal("red", "between", null, 50.0,80.0);

        assertEquals(10, total);
    }
    @Test
    public void testGetTotal_invalidOperation() {
        assertThrows(IllegalArgumentException.class, () -> socksService.getTotal("ret", "invalid", 5.0,null,null));
    }

    @Test
    public void testUpdateSocks_Success(){
        Socks newSocks = new Socks(null, "red", 50.0, 10);
        SocksEntity existingSocks = new SocksEntity(1L, "red", 60.0, 20);
        when(socksRepo.findById(1L)).thenReturn(Optional.of(existingSocks));

        socksService.updateSocks(1L, newSocks);

        verify(socksRepo, times(1)).save(existingSocks);

        assertEquals("red", existingSocks.getColor());
        assertEquals(10, existingSocks.getAmount());
        assertEquals(50.0, existingSocks.getPercentCotton());
    }
    @Test
    public void testUpdateSocks_NotFound(){
        Socks newSocks = new Socks(null, "red", 50.0, 10);
        when(socksRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, ()-> socksService.updateSocks(1L, newSocks));
    }

    @Test
    public void testSaveExcel_AddNewSocks() {
        List<Socks> socksList = List.of(new Socks(null, "red", 50.0, 10));
        when(socksRepo.findByColorAndPercentCotton("red", 50.0)).thenReturn(Optional.empty());

        socksService.saveExcel(socksList);

        verify(socksRepo, times(1)).save(any(SocksEntity.class));
    }

    @Test
    public void testSaveExcel_UpdateExistingSocks() {
        List<Socks> socksList = List.of(new Socks(null, "red", 50.0, 10));
        SocksEntity existingSocks = new SocksEntity(1L, "red", 50.0, 5);
        when(socksRepo.findByColorAndPercentCotton("red", 50.0)).thenReturn(Optional.of(existingSocks));

        socksService.saveExcel(socksList);

        verify(socksRepo, times(1)).save(existingSocks);
        assertEquals(15, existingSocks.getAmount());
    }
}