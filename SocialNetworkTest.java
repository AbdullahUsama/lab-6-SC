package twitter;

import static org.junit.Assert.*;
import java.time.Instant;
import java.util.*;
import org.junit.Test;

public class SocialNetworkTest {

    @Test(expected = AssertionError.class)
    public void test_assertions_enabled() {
        assert false; // Ensure assertions are enabled with VM argument: -ea
    }

    @Test
    public void test_guess_follows_graph_empty() {
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(new ArrayList<>());
        assertTrue("expected empty graph", followsGraph.isEmpty());
    }

    @Test
    public void test_guess_follows_graph_no_mentions() {
        List<Tweet> tweets = Arrays.asList(
            new Tweet(1, "abdullah", "Hello world!", Instant.now()),
            new Tweet(2, "usama", "Good morning!", Instant.now())
        );
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(tweets);
        assertEquals(2, followsGraph.size());
        assertTrue(followsGraph.get("abdullah").isEmpty());
        assertTrue(followsGraph.get("usama").isEmpty());
    }

    @Test
    public void test_guess_follows_graph_with_mentions() {
        List<Tweet> tweets = Arrays.asList(
            new Tweet(1, "abdullah", "Hello @usama!", Instant.now()),
            new Tweet(2, "usama", "Hi @abdullah!", Instant.now())
        );
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(tweets);

        assertEquals(2, followsGraph.size());
        assertTrue(followsGraph.get("abdullah").contains("usama"));
        assertTrue(followsGraph.get("usama").contains("abdullah"));
    }

    @Test
    public void test_guess_follows_graph_multiple_mentions() {
        List<Tweet> tweets = Arrays.asList(
            new Tweet(1, "Abdullah", "Hello @Usama and @Abdulrehman!", Instant.now()),
            new Tweet(2, "Usama", "Hi @Abdullah!", Instant.now())
        );
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(tweets);

        assertTrue(followsGraph.get("abdullah").contains("usama"));
        assertTrue(followsGraph.get("abdullah").contains("abdulrehman"));
    }

    @Test
    public void test_guess_follows_graph_multiple_tweets_by_same_author() {
        List<Tweet> tweets = Arrays.asList(
            new Tweet(1, "abdullah", "Hello @abdulrehman!", Instant.now()),
            new Tweet(2, "abdullah", "Also, @fatima is great.", Instant.now())
        );
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(tweets);

        assertEquals(2, followsGraph.get("ali").size());
        assertTrue(followsGraph.get("abdullah").contains("abdulrehman"));
        assertTrue(followsGraph.get("abdullah").contains("fatima"));
    }

    @Test
    public void test_influencers_empty() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        assertTrue("expected empty list", influencers.isEmpty());
    }

    @Test
    public void test_influencers_single_user_no_followers() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        followsGraph.put("abdullah", new HashSet<>());
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        assertTrue("expected empty list", influencers.isEmpty());
    }

    @Test
    public void test_influencers_single_influencer() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        followsGraph.put("abdullah", new HashSet<>(Arrays.asList("Fatima")));
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        assertEquals(1, influencers.size());
        assertEquals("abdulrehman", influencers.get(0).toLowerCase());
    }

    @Test
    public void test_influencers_multiple_users() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        followsGraph.put("abdullah", new HashSet<>(Arrays.asList("usama")));
        followsGraph.put("usama", new HashSet<>(Arrays.asList("abdullah", "abdulrehman")));
        followsGraph.put("abdulrehman", new HashSet<>(Arrays.asList("abdullah")));
        List<String> influencers = SocialNetwork.influencers(followsGraph);

        assertEquals(3, influencers.size()); // Corrected to check 3 influencers
    }

    @Test
    public void test_influencers_equal_followers() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        followsGraph.put("abdullah", new HashSet<>(Arrays.asList("usama", "abdulrehman")));
        followsGraph.put("usama", new HashSet<>(Arrays.asList("abdullah")));
        followsGraph.put("abdulrehman", new HashSet<>(Arrays.asList("abdullah")));
        List<String> influencers = SocialNetwork.influencers(followsGraph);

        assertEquals(3, influencers.size());
        assertTrue(influencers.contains("abdullah"));
    }
}
