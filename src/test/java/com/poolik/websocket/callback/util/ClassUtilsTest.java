package com.poolik.websocket.callback.util;

import com.poolik.websocket.callback.RequestHandler;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;
import org.junit.runner.RunWith;
import util.TestRequestHandler;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(Arquillian.class)
public class ClassUtilsTest {

  @Deployment
  public static WebArchive createDeployment() {
    WebArchive archive = ShrinkWrap.create(WebArchive.class)
        .addClasses(RequestHandler.class, TestRequestHandler.class, ClassUtils.class)
        .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");

    archive.addAsLibraries(Maven.resolver().resolve("com.poolik:classfinder:1.0").withTransitivity().asFile());
    return archive;
  }

  @Test
  public void findsRequestHandler() throws Exception {
    assertThat(ClassUtils.getImplementingInterface(RequestHandler.class).size(), is(1));
  }

}