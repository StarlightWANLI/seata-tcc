package cn.tedu.storage.service;

public interface StorageService {
    void decrease(Long productId, Integer count) throws Exception;
}
