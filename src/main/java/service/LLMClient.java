package service;

import entity.QuerySpec;


public interface LLMClient {

    QuerySpec extractQuerySpec(String userUtterance) throws Exception;
}
