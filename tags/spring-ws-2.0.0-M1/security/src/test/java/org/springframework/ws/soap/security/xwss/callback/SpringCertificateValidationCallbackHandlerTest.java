/*
 * Copyright 2005-2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.ws.soap.security.xwss.callback;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.X509Certificate;

import org.springframework.core.io.ClassPathResource;
import org.springframework.security.AuthenticationManager;
import org.springframework.security.BadCredentialsException;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.providers.TestingAuthenticationToken;
import org.springframework.security.providers.x509.X509AuthenticationToken;
import org.springframework.ws.soap.security.callback.CleanupCallback;

import com.sun.xml.wss.impl.callback.CertificateValidationCallback;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.easymock.EasyMock.*;

public class SpringCertificateValidationCallbackHandlerTest {

    private SpringCertificateValidationCallbackHandler callbackHandler;

    private AuthenticationManager authenticationManager;

    private X509Certificate certificate;

    private CertificateValidationCallback callback;

    @Before
    public void setUp() throws Exception {
        callbackHandler = new SpringCertificateValidationCallbackHandler();
        authenticationManager = createMock(AuthenticationManager.class);
        callbackHandler.setAuthenticationManager(authenticationManager);
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        InputStream is = null;
        try {
            is = new ClassPathResource("/org/springframework/ws/soap/security/xwss/test-keystore.jks").getInputStream();
            keyStore.load(is, "password".toCharArray());
        }
        finally {
            if (is != null) {
                is.close();
            }
        }
        certificate = (X509Certificate) keyStore.getCertificate("alias");
        callback = new CertificateValidationCallback(certificate);
    }

    @After
    public void tearDown() throws Exception {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void testValidateCertificateValid() throws Exception {
        expect(authenticationManager.authenticate(isA(X509AuthenticationToken.class)))
                .andReturn(new TestingAuthenticationToken(certificate, null, new GrantedAuthority[0]));

        replay(authenticationManager);

        callbackHandler.handleInternal(callback);
        boolean authenticated = callback.getResult();
        Assert.assertTrue("Not authenticated", authenticated);
        Assert.assertNotNull("No Authentication created", SecurityContextHolder.getContext().getAuthentication());

        verify(authenticationManager);
    }

    @Test
    public void testValidateCertificateInvalid() throws Exception {
        expect(authenticationManager.authenticate(isA(X509AuthenticationToken.class)))
                .andThrow(new BadCredentialsException(""));

        replay(authenticationManager);

        callbackHandler.handleInternal(callback);
        boolean authenticated = callback.getResult();
        Assert.assertFalse("Authenticated", authenticated);
        Assert.assertNull("Authentication created", SecurityContextHolder.getContext().getAuthentication());

        verify(authenticationManager);
    }

    @Test
    public void testCleanUp() throws Exception {
        TestingAuthenticationToken authentication =
                new TestingAuthenticationToken(new Object(), new Object(), new GrantedAuthority[0]);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        CleanupCallback cleanupCallback = new CleanupCallback();
        callbackHandler.handleInternal(cleanupCallback);
        Assert.assertNull("Authentication created", SecurityContextHolder.getContext().getAuthentication());
    }

}