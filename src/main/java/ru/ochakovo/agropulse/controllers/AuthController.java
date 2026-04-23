package ru.ochakovo.finpolicy.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.ochakovo.finpolicy.dto.CreateUserDto;
import ru.ochakovo.finpolicy.service.AuditLogService;
import ru.ochakovo.finpolicy.service.UserManagementService;

@Controller
@RequestMapping("api/auth")
@RequiredArgsConstructor
public final class AuthController {

     private final UserManagementService userManagementService;
     private final AuditLogService auditService;

//     @ResponseStatus(HttpStatus.CREATED)
//     @PostMapping("/registerCompany")
//     public void registerCompany(@RequestBody RegisterRequest request){
//          userManagementService.registerCompany(request.company(), request.user());
//     }

     @ResponseStatus(HttpStatus.CREATED)
     @PostMapping("/createUser")
     public void createUser(CreateUserDto registerUser){
          userManagementService.createUser(registerUser);
     }

//     @PatchMapping("/users/role")
//     public void changeRole(@RequestBody UpdateUserDto dto) {
//          userManagementService.updateUserRole(dto);
//     }

//     @DeleteMapping("/users/{id}")
//     public void deleteUser(@PathVariable Long id) {
//          userManagementService.deleteUser(id);
//     }

//     @GetMapping("/logs")
//     public List<AuditLog> getAllLogs(){
//          return auditService.findAll();
//     }
//
//     @GetMapping("/{id}")
//     public List<AuditLog> getByUser(@RequestParam Long id){
//          return auditService.findAllByUserId(id);
//     }
//
//     @GetMapping("/{action}")
//     public List<AuditLog> getByAction(@RequestParam String action){
//          return auditLogService.findAllByAction(action);
//     }
}