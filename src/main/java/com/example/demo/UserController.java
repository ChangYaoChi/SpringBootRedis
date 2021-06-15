package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public List<User> getAll() {
        return userService.getAllUsers();
    }

    @GetMapping("{id}")
    public User getById(
        @PathVariable(value = "id") int id
    ) {
        return userService.findById(id);
    }

    @GetMapping("{id}/{username}")
    public User getByIdAndUsername(
        @PathVariable(value = "id") int id,
        @PathVariable(value = "username") String username
    ) {
        return userService.findByIdAndUsername(id, username);
    }

    @PutMapping("{id}")
    public void updateById(
        @PathVariable(value = "id") int id,
        @RequestBody User user
    ) {
        userService.updateById(id, user);
    }

    @DeleteMapping("{id}")
    public void deleteById(@PathVariable(value = "id") int id) {
        userService.clear(id);
    }
}
