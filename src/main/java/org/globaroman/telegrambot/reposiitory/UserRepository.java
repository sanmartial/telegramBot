package org.globaroman.telegrambot.reposiitory;

import org.globaroman.telegrambot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
