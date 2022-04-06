package com.omar.vendingmachine.service;

import com.omar.vendingmachine.constants.UserContants;
import com.omar.vendingmachine.exceptions.InvalidDepositAmountException;
import com.omar.vendingmachine.exceptions.InvalidPurchaseException;
import com.omar.vendingmachine.model.user.Role;
import com.omar.vendingmachine.model.user.User;
import com.omar.vendingmachine.repository.UserRepository;
import com.omar.vendingmachine.repository.custom.CustomUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CustomUserDetailService implements UserDetailsService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    CustomUserRepository customUserRepository;
    @Autowired
    RoleService roleService;

    /**
     * Lists all the users in the users collection --> for testing purposes only.
     * @return
     */
    public List<User> listAll() {
        return userRepository.findAll().collectList().block();
    }

    /**
     * Hard deletes all the users in the users collection --> for testing purposes only.
     */
    public void deleteAll() {
        userRepository.deleteAll().block();
    }

    /**
     * Finds a user in the data base by the username, returns null in case no user with such username
     * @param username
     * @return
     */
    public User findByUsername(String username) {
        return customUserRepository.findByUsername(username).blockLast();
    }

    /**
     * Saves user to the database. Performs update in case the user exists and inserts a new user in case the user does not exist.
     * @param user
     */
    public void saveUser(User user) {
        userRepository.save(user).block();

    }

    /**
     * creates new user using the basic user parameters.
     * @param username
     * @param password
     * @param role
     * @param deposit
     */
    public void createUser(String username, String password, Role role, Integer deposit) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setRoles(new HashSet<>(Arrays.asList(role)));
        user.setDeposit(deposit);
        userRepository.save(user).block();
    }

    /**
     * Adds the deposit amount to the user with the input username
     * @param username
     * @param amount
     * @throws InvalidDepositAmountException in case the deposit amount is not among the allowed values.
     */
    public void depoist(String username, int amount) throws InvalidDepositAmountException {
        if (!UserContants.DEPOSIT_AMOUNTS.contains(amount)) {
            throw new InvalidDepositAmountException(String.format("Invalid deposit amount, the deposit amount should be among the values %s", UserContants.DEPOSIT_AMOUNTS));
        }
        User user = findByUsername(username);
        user.setDeposit(user.getDeposit() + amount);
        saveUser(user);
    }

    /**
     * Resets the deposit of the user with the input username to 0
     * @param username
     */
    public void resetDeposit(String username) {
        User user = findByUsername(username);
        user.setDeposit(0);
        saveUser(user);
    }

    /**
     * Completes the payment of the amount. If the amount is not availlable, throws exception
     *
     * @param username
     * @param amount
     * @return
     */
    public List<Integer> completePayment(String username, Integer amount) throws InvalidPurchaseException {
        User user = findByUsername(username);
        if (user.getDeposit() < amount) {
            throw new InvalidPurchaseException("User does not have suffecient funds");
        }
        int deposit = user.getDeposit();
        user.setDeposit(0);
        saveUser(user);
        return getChange(deposit, amount);
    }

    /**
     * Accepts the deposit that user has and the payment he shall pay and returns list of change coins
     *
     * @param deposit
     * @param payment
     * @return
     */
    private List<Integer> getChange(int deposit, int payment) {
        List<Integer> result = new ArrayList<>();
        deposit = deposit - payment;
        List<Integer> coinsTypes = new ArrayList<>(UserContants.DEPOSIT_AMOUNTS);
        coinsTypes.sort(Integer::compareTo);
        for (int i = coinsTypes.size() - 1; i >= 0; i--) {
            int times = deposit / coinsTypes.get(i);
            deposit = deposit - times * coinsTypes.get(i);
            result.addAll(Collections.nCopies(times, coinsTypes.get(i)));
        }
        return result;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = customUserRepository.findByUsername(username).blockLast();
        if (user != null) {
            List<GrantedAuthority> authorities = getUserAuthority(user.getRoles());
            return buildUserForAuthentication(user, authorities);
        } else {
            throw new UsernameNotFoundException(String.format("username %s is not found", username));
        }
    }

    /**
     * Returns the authorites granted to a user.
     * @param userRoles
     * @return
     */
    private List<GrantedAuthority> getUserAuthority(Set<Role> userRoles) {
        Set<GrantedAuthority> roles = new HashSet<>();
        userRoles.forEach(role -> {
            roles.add(new SimpleGrantedAuthority("ROLE_" + role.getName().toString()));
        });
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>(roles);
        return grantedAuthorities;
    }

    private UserDetails buildUserForAuthentication(User user, List<GrantedAuthority> authorities) {
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
    }
}
