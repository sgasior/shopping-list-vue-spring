package pl.edu.kopalniakodu.bootstrap;

import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import pl.edu.kopalniakodu.domain.*;
import pl.edu.kopalniakodu.repository.BillRepository;
import pl.edu.kopalniakodu.repository.OwnerRepository;
import pl.edu.kopalniakodu.repository.ProductRepository;
import pl.edu.kopalniakodu.repository.TaskRepository;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

//@Profile("dev")
@Component
@AllArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final TaskRepository taskRepository;
    private final OwnerRepository ownerRepository;
    private final ProductRepository productRepository;
    private final BillRepository billRepository;

    @Override
    public void run(String... args) throws Exception {
        generateBill();
        generateProducts();
        generateTasks();
        generatePersons();
    }

    private void generateBill() {
        billRepository.save(new Bill(BilanceType.INCOME, new BigDecimal("123")));
    }

    private void generateProducts() {
        productRepository.save(new Product("Chleb"));
        productRepository.save(new Product("Mleko"));
        productRepository.save(new Product("Pomidory"));

    }

    private void generatePersons() {
        Owner owner = new Owner();
        owner.setName("Szymaa");
        UUID savedOwnerID = ownerRepository.save(owner).getId();
        Owner ownerFound = ownerRepository.findById(savedOwnerID).get();
        for (Task task : taskRepository.findAll()) {
            ownerFound.getTasks().add(task);
        }
        ownerRepository.save(ownerFound);
    }

    private void generateTasks() {
        Task task = new Task();
        task.setTaskTitle("Zakupy dla Szymka");
        task.setCreatedDate(new Timestamp(System.currentTimeMillis()));
        task.setIsDone(false);

        Task task2 = new Task();
        task2.setTaskTitle("Zakupy dla Anetki");
        task2.setCreatedDate(new Timestamp(System.currentTimeMillis()));
        task2.setIsDone(false);
        taskRepository.save(task);
        taskRepository.save(task2);

        for (Product product : productRepository.findAll()) {
            task.getProducts().add(product);
        }

        task.setBill(billRepository.findAll().get(0));
        taskRepository.save(task);


    }
}
