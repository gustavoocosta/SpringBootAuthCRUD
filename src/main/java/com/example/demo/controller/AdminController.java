
package com.example.demo.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
public class AdminController {

    @GetMapping("/api/admin/secret")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Map<String,String> secret(){
        return Map.of("secret","only-admins-can-see-this");
    }
}
