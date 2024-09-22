package mk.ukim.finki.wp.kol2022.g3.service.impl;

import mk.ukim.finki.wp.kol2022.g3.model.ForumUser;
import mk.ukim.finki.wp.kol2022.g3.model.ForumUserType;
import mk.ukim.finki.wp.kol2022.g3.model.Interest;
import mk.ukim.finki.wp.kol2022.g3.model.exceptions.InvalidForumUserIdException;
import mk.ukim.finki.wp.kol2022.g3.model.exceptions.InvalidInterestIdException;
import mk.ukim.finki.wp.kol2022.g3.repository.ForumUserRepository;
import mk.ukim.finki.wp.kol2022.g3.repository.InterestRepository;
import mk.ukim.finki.wp.kol2022.g3.service.ForumUserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ForumUserImpl implements ForumUserService {
    private final ForumUserRepository forumUserRepository;
    private final InterestRepository interestRepository;
    private final PasswordEncoder passwordEncoder;

    public ForumUserImpl(ForumUserRepository forumUserRepository, InterestRepository interestRepository, PasswordEncoder passwordEncoder) {
        this.forumUserRepository = forumUserRepository;
        this.interestRepository = interestRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<ForumUser> listAll() {
        return forumUserRepository.findAll();
    }

    @Override
    public ForumUser findById(Long id) {
        return forumUserRepository.findById(id).orElseThrow(InvalidForumUserIdException::new);
    }

    @Override
    public ForumUser create(String name, String email, String password, ForumUserType type, List<Long> interestId, LocalDate birthday) {
        List<Interest> interests = interestId.stream().map(interest -> interestRepository.findById(interest).orElseThrow(InvalidInterestIdException::new)).collect(Collectors.toList());
        return forumUserRepository.save(new ForumUser(name,email,passwordEncoder.encode(password),type,interests,birthday));
    }

    @Override
    public ForumUser update(Long id, String name, String email, String password, ForumUserType type, List<Long> interestId, LocalDate birthday) {
        List<Interest> interests = interestId.stream().map(interest -> interestRepository.findById(interest).orElseThrow(InvalidInterestIdException::new)).collect(Collectors.toList());
        ForumUser found = forumUserRepository.findById(id).orElseThrow(InvalidForumUserIdException::new);
        found.setName(name);
        found.setEmail(email);
        found.setBirthday(birthday);
        found.setPassword(passwordEncoder.encode(password));
        found.setType(type);
        found.setInterests(interests);
        found.setBirthday(birthday);
        return forumUserRepository.save(found);
    }

    @Override
    public ForumUser delete(Long id) {
        ForumUser found = forumUserRepository.findById(id).orElseThrow(InvalidForumUserIdException::new);
        forumUserRepository.delete(found);
        return found;
    }

    @Override
    public List<ForumUser> filter(Long interestId, Integer age) {
        if (interestId != null && age !=null){
            LocalDate before = LocalDate.now().minusYears(age);
            Interest found = interestRepository.findById(interestId).orElseThrow(InvalidInterestIdException::new);
            return forumUserRepository.findAllByBirthdayBeforeAndInterestsContains(before,found);
        }
        else if (interestId == null && age != null){
            LocalDate before = LocalDate.now().minusYears(age);
            return forumUserRepository.findAllByBirthdayBefore(before);
        }
        else if (interestId != null && age == null){
            Interest found = interestRepository.findById(interestId).orElseThrow(InvalidInterestIdException::new);
            return forumUserRepository.findAllByInterestsContains(found);
        }
        else
            return forumUserRepository.findAll();
    }
}
