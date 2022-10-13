package co.edu.udea.backend.controller;

import co.edu.udea.backend.model.Home;
import co.edu.udea.backend.service.HomeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/home")
public class HomeController {

    private final HomeService homeService;

    public HomeController(HomeService homeService) {
        this.homeService = homeService;
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

}
