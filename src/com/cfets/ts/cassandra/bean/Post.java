package com.cfets.ts.cassandra.bean;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import com.cfets.ts.cassandra.CassandraFactory;
import com.datastax.driver.core.BatchStatement;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.utils.UUIDs;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import com.datastax.driver.mapping.Result;
import com.datastax.driver.mapping.annotations.ClusteringColumn;
import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.PartitionKey;
import com.datastax.driver.mapping.annotations.Table;
import com.google.common.base.Objects;

@Table(name = "posts")
public class Post {

    private String title;
    private String content;
    private InetAddress device;

    @ClusteringColumn
    @Column(name = "post_id")
    private UUID postId;

    @PartitionKey
    @Column(name = "user_id")
    private int userId;


    private Set<String> tags;

    public Post() {
    }

    public Post(User user, String title) {
        this.userId = user.getUserId();
        this.postId = UUIDs.timeBased();
        this.title = title;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public UUID getPostId() {
        return postId;
    }

    public void setPostId(UUID postId) {
        this.postId = postId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public InetAddress getDevice() {
        return device;
    }

    public void setDevice(InetAddress device) {
        this.device = device;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null || other.getClass() != this.getClass())
            return false;

        Post that = (Post) other;
        return Objects.equal(userId, that.userId)
                && Objects.equal(postId, that.postId)
                && Objects.equal(title, that.title)
                && Objects.equal(content, that.content)
                && Objects.equal(device, that.device)
                && Objects.equal(tags, that.tags);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(userId, postId, title, content, device, tags);
    }
    
    public static void main(String[] args) throws UnknownHostException, InterruptedException, ExecutionException{
    	List<InetSocketAddress> sockets = new ArrayList<InetSocketAddress>();
		InetSocketAddress socket = new InetSocketAddress("127.0.0.1", 9042);
		sockets.add(socket);
		Session session = CassandraFactory.getSyncSession(sockets,
				"damon_space");
    	MappingManager manager = new MappingManager(session);

        Mapper<Post> m = manager.mapper(Post.class);

        User u1 = new User(1,"Paul", "paul@gmail.com");
        Post p1 = new Post(u1, "Something about mapping");
        Post p2 = new Post(u1, "Something else");
        Post p3 = new Post(u1, "Something more");

        p1.setDevice(InetAddress.getLocalHost());

        p2.setTags(new HashSet<String>(Arrays.asList("important", "keeper")));

        m.save(p1);
        m.save(p2);
        m.save(p3);

        // Creates the accessor proxy defined above
        PostAccessor postAccessor = manager.createAccessor(PostAccessor.class);

        // Note that getOne is really the same than m.get(), it's just there
        // for demonstration sake.
        Post p = postAccessor.getOne(p1.getUserId(), p1.getPostId());

        Result<Post> r = postAccessor.getAllAsync(p1.getUserId()).get();

        // No argument call
        r = postAccessor.getAll();

        BatchStatement batch = new BatchStatement();
        batch.add(postAccessor.updateContentQuery("Something different", p1.getUserId(), p1.getPostId()));
        batch.add(postAccessor.updateContentQuery("A different something", p2.getUserId(), p2.getPostId()));
        manager.getSession().execute(batch);

        Post p1New = m.get(p1.getUserId(), p1.getPostId());
        Post p2New = m.get(p2.getUserId(), p2.getPostId());

        m.delete(p1);
        m.delete(p2);

        // Check delete by primary key too
        m.delete(p3.getUserId(), p3.getPostId());
    }
}

