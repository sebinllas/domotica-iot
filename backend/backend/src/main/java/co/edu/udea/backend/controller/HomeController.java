package co.edu.udea.backend.controller;

import co.edu.udea.backend.model.Home;
import co.edu.udea.backend.model.SensorMessage;
import co.edu.udea.backend.service.HomeService;
import co.edu.udea.backend.service.SensorMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/home")
@CrossOrigin(origins = "*")
public class HomeController {

    private final HomeService homeService;
    private final SensorMessageService sensorMessageService;

    public HomeController(HomeService homeService, SensorMessageService sensorMessageService) {

        this.homeService = homeService;
        this.sensorMessageService = sensorMessageService;
    }

    @Operation(description = "View a list of available homes")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list"),
            @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
            @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
    })
    @GetMapping()
    public List<Home> getAllDevices() {
        return homeService.findAllHomes();
    }

    @Operation(description = "Register a new home")
    @PostMapping()
    public Home registerHome(@RequestBody Home home) {
        return homeService.registerHome(home);
    }

    @Operation(description = "Send a message to a specific device in a specific home")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list"),
            @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
            @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
    })
    @PostMapping("/{homeName}/{deviceName}/{message}")
    public void sendMessage(@PathVariable String homeName, @PathVariable String deviceName, @PathVariable String message) {
        homeService.sendMessage(homeName, deviceName, message);
    }

    @Operation(description = "Get temperature data in a specific home")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list"),
            @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
            @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
    })
    @GetMapping("/temp/{homeName}")
    public List<SensorMessage> getTemperatureHistory(@PathVariable String homeName) {
        return sensorMessageService.getMessagesByHomeNameAndDeviceName(homeName, "temp1");
    }

    @Operation(description = "Get light data in a specific home")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list"),
            @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
            @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
    })
    @GetMapping("/light/{homeName}")
    public List<SensorMessage> getLightHistory(@PathVariable String homeName) {
        return sensorMessageService.getMessagesByHomeNameAndDeviceName(homeName, "light1");
    }

}
