package dao;

public interface UserInConferenceDao {
    boolean isPresent(int userId, int conferenceId);
}
