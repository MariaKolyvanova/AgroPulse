package ru.ochakovo.finpolicy.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.ochakovo.finpolicy.database.entity.organization.AuditLog;
import ru.ochakovo.finpolicy.database.repository.UserRepository;
import ru.ochakovo.finpolicy.dto.ChangeRoleDto;
import ru.ochakovo.finpolicy.dto.RegisterRequest;
import ru.ochakovo.finpolicy.dto.RegisterUserDto;
import ru.ochakovo.finpolicy.service.AuditService;
import ru.ochakovo.finpolicy.service.UserManagementService;

import java.util.List;

@Controller
@RequestMapping("api/auth")
@RequiredArgsConstructor
public final class AuthController {

     private final UserManagementService userManagementService;
     private final AuditService auditService;

     @ResponseStatus(HttpStatus.CREATED)
     @PostMapping("/registerCompany")
     public void registerCompany(@RequestBody RegisterRequest request){
          userManagementService.registerCompany(request.company(), request.user());
     }

     @ResponseStatus(HttpStatus.CREATED)
     @PostMapping("/registerUser")
     public void registerUser(RegisterUserDto registerUser){
          userManagementService.registerUser(registerUser);
     }

     @PatchMapping("/users/role")
     public void changeRole(@RequestBody ChangeRoleDto dto) {
          userManagementService.changeUserRole(dto);
     }

     @DeleteMapping("/users/{id}")
     public void deleteUser(@PathVariable Long id) {
          userManagementService.deleteUser(id);
     }

     @GetMapping("/logs")
     public List<AuditLog> getAllLogs(){
          return auditService.findAll();
     }

     @GetMapping("/{id}")
     public List<AuditLog> getByUser(@RequestParam Long id){
          return auditService.findAllByUserId(id);
     }

     @GetMapping("/{action}")
     public List<AuditLog> getByAction(@RequestParam String action){
          return auditService.findAllByAction(action);
     }
}