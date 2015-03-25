package io.katharsis.path;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Arrays;

public class PathBuilderTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void onEmptyPathShouldThrowException() {
        // GIVEN
        String path = "/";
        PathBuilder sut = new PathBuilder();

        // THEN
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Path is empty");

        // WHEN
        sut.buildPath(path);
    }

    @Test
    public void onFlatResourcePathShouldReturnFlatPath() {
        // GIVEN
        String path = "/resource1/";
        PathBuilder sut = new PathBuilder();

        // WHEN
        JsonPath jsonPath = sut.buildPath(path);

        // THEN
        JsonPath expectedPath = new JsonPath("resource1");
        Assert.assertEquals(expectedPath, jsonPath);
        Assert.assertTrue(expectedPath.isCollection());
    }

    @Test
    public void onFlatResourceInstancePathShouldReturnFlatPath() {
        // GIVEN
        String path = "/resource1/1";
        PathBuilder sut = new PathBuilder();

        // WHEN
        JsonPath jsonPath = sut.buildPath(path);

        // THEN
        JsonPath expectedPath = new JsonPath("resource1", false, new PathIds("1"));
        Assert.assertEquals(expectedPath, jsonPath);
        Assert.assertFalse(expectedPath.isCollection());
    }

    @Test
    public void onNestedResourcePathShouldReturnNestedPath() {
        // GIVEN
        String path = "/resource1/1/resource2";
        PathBuilder sut = new PathBuilder();

        // WHEN
        JsonPath jsonPath = sut.buildPath(path);

        // THEN
        JsonPath expectedPath = new JsonPath("resource2");
        expectedPath.setParentResource(new JsonPath("resource1", false, new PathIds("1")));
        Assert.assertEquals(expectedPath, jsonPath);
    }

    @Test
    public void onNestedResourceInstancePathShouldReturnNestedPath() {
        // GIVEN
        String path = "/resource1/1/resource2/2";
        PathBuilder sut = new PathBuilder();

        // WHEN
        JsonPath jsonPath = sut.buildPath(path);

        // THEN
        JsonPath expectedPath = new JsonPath("resource2", false, new PathIds("2"));
        expectedPath.setParentResource(new JsonPath("resource1", false, new PathIds("1")));
        Assert.assertEquals(expectedPath, jsonPath);
    }

    @Test
    public void onNestedResourceRelationshipPathShouldReturnNestedPath() {
        // GIVEN
        String path = "/resource1/1/links/resource2/";
        PathBuilder sut = new PathBuilder();

        // WHEN
        JsonPath jsonPath = sut.buildPath(path);

        // THEN
        JsonPath expectedPath = new JsonPath("resource2", false);
        expectedPath.setParentResource(new JsonPath("resource1", true, new PathIds("1")));
        Assert.assertEquals(expectedPath, jsonPath);
    }

    @Test
    public void onNestedWrongResourceRelationshipPathShouldThrowException() {
        // GIVEN
        String path = "/resource1/1/links/";
        PathBuilder sut = new PathBuilder();

        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("No type field defined after links marker");

        // WHEN
        sut.buildPath(path);

        // THEN - EXCEPTION
    }

    @Test
    public void onMultipleResourceInstancesPathShouldReturnCollectionPath() {
        // GIVEN
        String path = "/resource1/1,2";
        PathBuilder sut = new PathBuilder();

        // WHEN
        JsonPath jsonPath = sut.buildPath(path);

        // THEN
        Assert.assertTrue(jsonPath.isCollection());
        Assert.assertEquals(jsonPath.getIds().getIds(), Arrays.asList("1", "2"));
    }
}
