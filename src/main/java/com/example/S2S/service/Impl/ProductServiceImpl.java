package com.example.S2S.service.Impl;

import com.example.S2S.common.Enum.Status;
import com.example.S2S.dto.AI.AiResult;
import com.example.S2S.entity.Product;
import com.example.S2S.entity.Transaction;
import com.example.S2S.entity.User;
import com.example.S2S.repository.ProductRepository;
import com.example.S2S.repository.TransactionRepository;
import com.example.S2S.repository.UserRepository;
import com.example.S2S.service.AiModerationService;
import com.example.S2S.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final AiModerationService aiModerationService;

    @Override
    public Product createProduct(Product product) {
        product.setStatus(Status.DRAFT);
        product.setCreatedAt(LocalDateTime.now());
        return productRepository.save(product);
    }

    @Override
    public Product submitProduct(String productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // AI moderation
        AiResult aiResult = aiModerationService.analyze(product);
        product.setAiStatus(aiResult.status());
        product.setAiNote(aiResult.note());

        product.setStatus(Status.PENDING);
        return productRepository.save(product);
    }

    @Override
    public Product approveProduct(String productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        product.setStatus(Status.APPROVED);
        return productRepository.save(product);
    }

    @Override
    public Product confirmBuyer(String productId, String buyerId) {
        Product product = productRepository.findByIdWithLock(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (product.getStatus() == Status.SOLD) {
            throw new RuntimeException("Product already sold");
        }

        User buyer = userRepository.findById(buyerId)
                .orElseThrow(() -> new RuntimeException("Buyer not found"));

        Transaction transaction = Transaction.builder()
                .product(product)
                .buyer(buyer)
                .status(Transaction.Status.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        transactionRepository.save(transaction);

        product.setStatus(Status.SOLD);
        return productRepository.save(product);
    }

    @Override
    public Page<Product> getApprovedProducts(Pageable pageable) {
        return productRepository.findByStatus(Status.APPROVED, pageable);
    }
}

