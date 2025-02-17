package com.eta.houzezbackend.controller;

import com.eta.houzezbackend.dto.AgentGetDto;
import com.eta.houzezbackend.dto.AgentSignUpDto;
import com.eta.houzezbackend.dto.PropertyPostDto;
import com.eta.houzezbackend.dto.PropertyGetDto;
import com.eta.houzezbackend.dto.PatchPasswordDto;
import com.eta.houzezbackend.model.Agent;
import com.eta.houzezbackend.service.AgentService;
import com.eta.houzezbackend.service.PropertyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("agents")
@RequiredArgsConstructor
public class AgentController {

    private final AgentService agentService;
    private final PropertyService propertyService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AgentGetDto signUp(@Valid @RequestBody AgentSignUpDto agentSignUpDto) {
        return agentService.signUpNewAgent(agentSignUpDto);
    }

    @PostMapping("/sign-in")
    @ResponseStatus(HttpStatus.OK)
    public AgentGetDto signIn(@RequestAttribute String username) {
        return agentService.signIn(username);
    }

    @GetMapping("/{id}")
    public AgentGetDto getAgent(@PathVariable Long id) {
        return agentService.getAgent(id);
    }


    @PatchMapping("/{id}")
    public Agent activeAgent(@RequestParam String token, @PathVariable Long id) {
        return agentService.setAgentToActive(token);
    }

    @PatchMapping("/password")
    public AgentGetDto patchPassword(@Valid @RequestBody PatchPasswordDto patchPasswordDto) {
        return agentService.patchPassword(patchPasswordDto);
    }


    @RequestMapping(method = {RequestMethod.HEAD})
    public void getAgentByEmail(@RequestParam String email) {
        agentService.findByEmail(email);
    }

    @PostMapping("/forget-password")
    @ResponseStatus(HttpStatus.OK)
    public void sendResetPasswordEmail(@RequestBody Map<String, String> map) {
        agentService.sendForgetPasswordEmail(map.get("email"));
    }

    @PostMapping("/resend-email")
    @ResponseStatus(HttpStatus.OK)
    public void resendEmail(@RequestBody Map<String, String> map) {
        agentService.resendEmail(map.get("email"));
    }

    @PostMapping("/{id}/properties")
    @ResponseStatus(HttpStatus.CREATED)
    public PropertyGetDto addProperty(@Valid @RequestBody PropertyPostDto propertyCreateDto, @PathVariable long id) {
        return propertyService.createNewProperty(propertyCreateDto, id);
    }

    @GetMapping("/{id}/properties")
    public List<PropertyGetDto> getPropertiesByAgent(@PathVariable long id, @RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "10") int size) {
        return propertyService.getPropertiesByAgent(id, page, size);
    }
}
