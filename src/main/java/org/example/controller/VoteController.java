package org.example.controller;

import lombok.AllArgsConstructor;
import org.example.dto.VoteResponceDTO;
import org.example.dto.VoteStatsResponceDTO;
import org.example.exception.*;
import org.example.manager.VoteManager;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
public class VoteController {
    private VoteManager manager;

    @RequestMapping("/votes.make")
    public VoteResponceDTO make(long id, int rating) throws NotAuthenticatedException, PasswordNotMatchesException, ForbiddenException, InvalidRatingException, InvalidVoteException, VoteAlreadyMadeException {
        return manager.make(id, rating);
    }

    @RequestMapping("/votes.stats")
    public List<VoteStatsResponceDTO> stats(long limit, long offset) throws ForbiddenException, NotAuthenticatedException, PasswordNotMatchesException {
        return manager.stats(limit, offset);
    }
}
