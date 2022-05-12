package org.example.controller;

import lombok.AllArgsConstructor;
import org.example.dto.*;
import org.example.exception.*;
import org.example.manager.TicketManager;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@AllArgsConstructor
public class TicketController {
    private TicketManager manager;

    @RequestMapping("/tickets.getAll")
    public List<TicketGetAllResponceDTO> getAll(long limit, long offset) throws NotAuthenticatedException, PasswordNotMatchesException {
        return manager.getAll(limit, offset);
    }

    @RequestMapping("/tickets.getById")
    public TicketGetByIdResponceDTO getById(long id) throws TicketNotFoundException {
        return manager.getById(id);
    }

    @RequestMapping("/media/uploadData")
    public MediaUploadResponseDTO uploadBytes(@RequestBody byte[] data) throws IOException, ForbiddenException, NotAuthenticatedException, PasswordNotMatchesException {
        return manager.upload(data);
    }

    @RequestMapping("/tickets.create")
    public TicketCreateResponceDTO create(TicketCreateRequestDTO requestDTO) throws ForbiddenException, NotAuthenticatedException, PasswordNotMatchesException, InvalidDescriptionException {
        return manager.create(requestDTO);
    }

    @RequestMapping("/tickets.updateAccepted")
    public TicketUpdateAcceptedResponceDTO updateAccepted(TicketUpdateAcceptedRequestDTO requestDTO) throws ForbiddenException, NotAuthenticatedException, PasswordNotMatchesException, InvalidSlaException, TicketNotFoundException {
        return manager.updateAccepted(requestDTO);
    }

    @RequestMapping("/tickets.updateDecided")
    public TicketUpdateDecidedResponceDTO updateDecided(TicketUpdateDecidedRequestDTO requestDTO) throws ForbiddenException, NotAuthenticatedException, PasswordNotMatchesException, TicketNotFoundException, InvalidSolutionException {
        return manager.updateDecided(requestDTO);
    }

    @RequestMapping("/tickets.updateArchivedById")
    public TicketUpdateArchivedByIdResponceDTO updateArchivedById(long id) throws ForbiddenException, NotAuthenticatedException, PasswordNotMatchesException, TicketNotFoundException {
        return manager.updateArchivedById(id);
    }

    @RequestMapping("/tickets.selectByTypeSla")
        public List<TicketSelectByTypeSlaResponceDTO> selectByTypeSla(int sla_id, long limit, long offset) throws ForbiddenException, NotAuthenticatedException, PasswordNotMatchesException, InvalidSlaException {
            return manager.selectByTypeSla(sla_id, limit, offset);
    }

    @RequestMapping("/tickets.selectAcceptedAll")
        public List<TicketSelectAcceptedAllResponceDTO> selectAcceptedAll(long limit, long offset) throws ForbiddenException, NotAuthenticatedException, PasswordNotMatchesException {
            return manager.selectAcceptedAll(limit, offset);
    }

    @RequestMapping("/tickets.selectDecided")
    public List<TicketSelectDecidedResponceDTO> selectDecided(long limit, long offset) throws ForbiddenException, NotAuthenticatedException, PasswordNotMatchesException {
        return manager.selectDecided(limit, offset);
    }

    @RequestMapping("/tickets.selectOverdue")
        public List<TicketSelectOverdueResponceDTO> selectOverdue(long limit, long offset) throws ForbiddenException, NotAuthenticatedException, PasswordNotMatchesException {
            return manager.selectOverdue(limit, offset);
    }

    @RequestMapping("/tickets.resolutionRate")
        public TicketResolutionRateResponceDTO resolutionRate() throws ForbiddenException, NotAuthenticatedException, PasswordNotMatchesException {
            return manager.resolutionRate();
    }
}
