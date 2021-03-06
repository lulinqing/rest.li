/*
   Copyright (c) 2012 LinkedIn Corp.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

package com.linkedin.restli.examples;


import java.util.Collections;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.linkedin.r2.RemoteInvocationException;
import com.linkedin.r2.transport.common.Client;
import com.linkedin.r2.transport.common.bridge.client.TransportClientAdapter;
import com.linkedin.r2.transport.http.client.HttpClientFactory;
import com.linkedin.restli.client.ActionRequest;
import com.linkedin.restli.client.Response;
import com.linkedin.restli.client.ResponseFuture;
import com.linkedin.restli.client.RestClient;
import com.linkedin.restli.client.RestLiResponseException;
import com.linkedin.restli.common.HttpStatus;
import com.linkedin.restli.examples.greetings.client.ActionsBuilders;

public class TestActionsResource extends RestLiIntegrationTest
{
  private static final Client CLIENT = new TransportClientAdapter(new HttpClientFactory().getClient(Collections.<String, String>emptyMap()));
  private static final String URI_PREFIX = "http://localhost:1338/";
  private static final RestClient REST_CLIENT = new RestClient(CLIENT, URI_PREFIX);
  private static final ActionsBuilders ACTIONS_BUILDERS = new ActionsBuilders();

  @BeforeClass
  public void initClass() throws Exception
  {
    super.init();
  }

  @AfterClass
  public void shutDown() throws Exception
  {
    super.shutdown();
  }

  @Test
  public void testPrimitiveReturningActions() throws RemoteInvocationException
  {
    ActionRequest<Integer> intRequest = ACTIONS_BUILDERS.actionReturnInt().build();
    Integer integer = REST_CLIENT.sendRequest(intRequest).getResponse().getEntity();
    Assert.assertEquals(0, integer.intValue());

    ActionRequest<Boolean> boolRequest = ACTIONS_BUILDERS.actionReturnBool().build();
    Boolean bool = REST_CLIENT.sendRequest(boolRequest).getResponse().getEntity();
    Assert.assertTrue(bool);
  }

  @Test
  public void testActionsSet() throws RemoteInvocationException
  {
    ActionRequest<Integer> request = ACTIONS_BUILDERS.actionUltimateAnswer().build();
    Integer answer = REST_CLIENT.sendRequest(request).getResponse().getEntity();
    Assert.assertEquals(answer, Integer.valueOf(42));
  }

  @Test
  public void testActionNamedGet() throws RemoteInvocationException
  {
    ActionRequest<String> request = ACTIONS_BUILDERS.actionGet().build();
    String value = REST_CLIENT.sendRequest(request).getResponse().getEntity();
    Assert.assertEquals(value, "Hello, World");
  }

  // Not implemented until we switch back to using the "useContinuation" path by default
  // in the AbstractR2Servlet.
  @Test(groups = TestConstants.TESTNG_GROUP_NOT_IMPLEMENTED)
  //@Test
  public void testServerTimeout() throws RemoteInvocationException
  {
    ActionRequest<Void> request = ACTIONS_BUILDERS.actionTimeout().build();
    try
    {
      REST_CLIENT.sendRequest(request).getResponse();
    }
    catch (RestLiResponseException e)
    {
      Assert.assertEquals(e.getStatus(), HttpStatus.S_500_INTERNAL_SERVER_ERROR.getCode());
      Assert.assertTrue(e.getResponse().toString().contains("Server Timeout"));
    }
  }

  @Test
  public void testParSeqAction() throws RemoteInvocationException
  {
    // this version is given a Context and returns a promise
    ActionRequest<String> req =
        ACTIONS_BUILDERS.actionParseq().paramA(5).paramB("yay").paramC(false).build();
    ResponseFuture<String> future = REST_CLIENT.sendRequest(req);
    Response<String> response = future.getResponse();

    Assert.assertEquals(response.getEntity(), "101 YAY false");
  }

  @Test
  public void testParSeqAction2() throws RemoteInvocationException
  {
    // this version gives a Task that RestLi runs
    ActionRequest<String> req =
        ACTIONS_BUILDERS.actionParseq2().paramA(5).paramB("yay").paramC(false).build();
    ResponseFuture<String> future = REST_CLIENT.sendRequest(req);
    Response<String> response = future.getResponse();

    Assert.assertEquals(response.getEntity(), "101 YAY false");
  }

  @Test(expectedExceptions = RestLiResponseException.class)
  public void testFailPromiseCall() throws RemoteInvocationException
  {
    // this version gives a Task that RestLi runs
    ActionRequest<Void> req = ACTIONS_BUILDERS.actionFailPromiseCall().build();
    REST_CLIENT.sendRequest(req).getResponse();
  }

  @Test(expectedExceptions = RestLiResponseException.class)
  public void testFailPromiseThrow() throws RemoteInvocationException
  {
    // this version gives a Task that RestLi runs
    ActionRequest<Void> req = ACTIONS_BUILDERS.actionFailPromiseThrow().build();
    REST_CLIENT.sendRequest(req).getResponse();
  }

  @Test(expectedExceptions = RestLiResponseException.class)
  public void testFailTaskCall() throws RemoteInvocationException
  {
    // this version gives a Task that RestLi runs
    ActionRequest<Void> req = ACTIONS_BUILDERS.actionFailTaskCall().build();
    REST_CLIENT.sendRequest(req).getResponse();
  }

  @Test(expectedExceptions = RestLiResponseException.class)
  public void testFailTaskThrow() throws RemoteInvocationException
  {
    // this version gives a Task that RestLi runs
    ActionRequest<Void> req = ACTIONS_BUILDERS.actionFailTaskThrow().build();
    REST_CLIENT.sendRequest(req).getResponse();
  }

  @Test(expectedExceptions = RestLiResponseException.class)
  public void testFailThrowInTask() throws RemoteInvocationException
  {
    // this version gives a Task that RestLi runs
    ActionRequest<Void> req = ACTIONS_BUILDERS.actionFailThrowInTask().build();
    REST_CLIENT.sendRequest(req).getResponse();
  }

  @Test(expectedExceptions = RestLiResponseException.class)
  public void testNullPromise() throws RemoteInvocationException
  {
    // this version gives a Task that RestLi runs
    ActionRequest<String> req = ACTIONS_BUILDERS.actionNullPromise().build();
    REST_CLIENT.sendRequest(req).getResponse();
  }

  @Test(expectedExceptions = RestLiResponseException.class)
  public void testNullTask() throws RemoteInvocationException
  {
    // this version gives a Task that RestLi runs
    ActionRequest<String> req = ACTIONS_BUILDERS.actionNullTask().build();
    REST_CLIENT.sendRequest(req).getResponse();
  }
}
