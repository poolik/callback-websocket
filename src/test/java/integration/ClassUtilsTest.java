package integration;

import com.poolik.websocket.callback.WebSocketRequestHandler;
import com.poolik.websocket.callback.util.ClassUtils;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;
import org.junit.runner.RunWith;
import util.TestWebSocketRequestHandler;

import static org.hamcrest.core.AnyOf.anyOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(Arquillian.class)
public class ClassUtilsTest {

  @Deployment
  public static WebArchive createDeployment() {
    WebArchive archive = ShrinkWrap.create(WebArchive.class)
        .addClasses(WebSocketRequestHandler.class, TestWebSocketRequestHandler.class, ClassUtils.class)
        .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");

    archive.addAsLibraries(Maven.resolver().resolve("com.poolik:classfinder:1.0").withTransitivity().asFile());
    return archive;
  }

  @Test
  public void findsRequestHandlerImplementations() throws Exception {
    assertThat(ClassUtils.getImplementingInterface(WebSocketRequestHandler.class).size(), anyOf(is(1), is(2)));
  }
}