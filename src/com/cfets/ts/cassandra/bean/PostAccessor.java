package com.cfets.ts.cassandra.bean;

import java.util.UUID;

import com.datastax.driver.core.Statement;
import com.datastax.driver.mapping.Result;
import com.datastax.driver.mapping.annotations.Accessor;
import com.datastax.driver.mapping.annotations.Param;
import com.datastax.driver.mapping.annotations.Query;
import com.datastax.driver.mapping.annotations.QueryParameters;
import com.google.common.util.concurrent.ListenableFuture;

@Accessor
public interface PostAccessor {
	@Query("SELECT * FROM posts WHERE user_id=:userId AND post_id=:postId")
    Post getOne(@Param("userId") int userId,
                @Param("postId") UUID postId);

    // Note that the following method will be asynchronous (it will use executeAsync
    // underneath) because it's return type is a ListenableFuture. Similarly, we know
    // that we need to map the result to the Post entity thanks to the return type.
    @Query("SELECT * FROM posts WHERE user_id=?")
    ListenableFuture<Result<Post>> getAllAsync(int userId);

    // The method above actually query stuff, but if a method is declared to return
    // a Statement, it will not execute anything, but just return you the BoundStatement
    // ready for execution. That way, you can batch stuff for instance (see usage below).
    @Query("UPDATE posts SET content=? WHERE user_id=? AND post_id=?")
    Statement updateContentQuery(String newContent, int userId, UUID postId);

    @Query("SELECT * FROM posts")
    Result<Post> getAll();

    @Query("SELECT * FROM posts")
    @QueryParameters(idempotent = true)
    Statement getAllAsStatementIdempotent();

    @Query("SELECT * FROM posts")
    @QueryParameters(idempotent = false)
    Statement getAllAsStatementNonIdempotent();

    @Query("SELECT * FROM posts")
    Statement getAllAsStatement();
}