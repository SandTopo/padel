package com.ceica.padel.Controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;

@org.springframework.web.bind.annotation.RestController
public class RestController {

    private final ReservaController reservaController;

    @Autowired
    public RestController(ReservaController reservaController){
        this.reservaController = reservaController;
    }

    @GetMapping("/isLogged")
    public Boolean isLogged() {
        return reservaController.getUserLogged() != null;
    }
}
