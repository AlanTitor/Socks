package com.AlanTitor.sockTests.ControllerTest;

import com.AlanTitor.socks.Controller.SocksController;
import com.AlanTitor.socks.DTO.Socks;
import com.AlanTitor.socks.Main;
import com.AlanTitor.socks.Service.SocksService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SocksController.class)
@ContextConfiguration(classes = Main.class)
public class SocksControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SocksService socksService;


    @Test
    public void testIncomeSocs_Success() throws Exception {
        Socks socks = new Socks(null, "red", 50.0, 12);

        mockMvc.perform(post("/api/socks/income").contentType(MediaType.APPLICATION_JSON)
                .content("{\"color\":\"red\",\"percentCotton\":50.0,\"amount\":10}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Income is OK"));

        verify(socksService, times(1)).registerIncome(any(Socks.class));
    }

    @Test
    public void testIncomeSocs_Error() throws Exception {
        mockMvc.perform(post("/api/socks/income").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"color\":null,\"percentCotton\":50.0,\"amount\":10}"))
                        .andExpect(status().isBadRequest());

        verify(socksService, never()).registerIncome(any(Socks.class));
    }
    @Test
    public void testOutcomeSocks() throws Exception {
        Socks socks = new Socks(null, "red", 50.0, 5);

        mockMvc.perform(post("/api/socks/outcome")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"color\":\"red\",\"percentCotton\":50.0,\"amount\":5}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Outcome is OK"));

        verify(socksService, times(1)).registerOutcome(any(Socks.class));
    }

    @Test
    public void testOutcomeSocks_InvalidRequest() throws Exception {
        mockMvc.perform(post("/api/socks/outcome")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"color\":null,\"percentCotton\":50.0,\"amount\":5}"))
                .andExpect(status().isBadRequest());

        verify(socksService, never()).registerOutcome(any(Socks.class));
    }
    @Test
    public void testUpdateSocks() throws Exception {
        Socks socks = new Socks(null, "red", 50.0, 10);

        mockMvc.perform(put("/api/socks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"color\":\"red\",\"percentCotton\":50.0,\"amount\":10}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Socks with id: 1 have been changed!"));

        verify(socksService, times(1)).updateSocks(eq(1L), any(Socks.class));
    }

    @Test
    public void testUploadExcel_InvalidFileFormat() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "test data".getBytes());

        mockMvc.perform(multipart("/api/socks/batch").file(file))
                .andExpect(status().isBadRequest());

        verify(socksService, never()).saveExcel(anyList());
    }

}
