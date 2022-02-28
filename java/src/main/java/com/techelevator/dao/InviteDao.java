package com.techelevator.dao;

import com.techelevator.model.Invite;
import com.techelevator.model.User;

import java.util.List;

public interface InviteDao {

    public void invitePlayer(Long userId, int gameId);

    public void confirmInvite(int userId, int gameId);

    public List<Invite> displayInvites(int userId);

    public User getUserByEmail(String email);

    public void declineInvite(int userId, int gameId);
}
