package org.aytsan_lex.twitchbot;

import java.util.List;
import java.util.Optional;

import com.github.philippheuer.credentialmanager.CredentialManager;
import com.github.philippheuer.credentialmanager.api.IStorageBackend;
import com.github.philippheuer.credentialmanager.domain.AuthenticationController;
import com.github.philippheuer.credentialmanager.domain.Credential;
import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.credentialmanager.identityprovider.OAuth2IdentityProvider;

public class TwitchBotCredentialManager extends CredentialManager
{
    private static class TwitchBotCredentialsStorage implements IStorageBackend
    {
        @Override
        public List<Credential> loadCredentials()
        {
            return List.of();
        }

        @Override
        public void saveCredentials(List<Credential> credentials)
        {
            credentials.forEach(credential -> {
                final String userId = credential.getUserId();
                final String accessToken = ((OAuth2Credential) credential).getAccessToken();
                final String refreshToken = ((OAuth2Credential) credential).getRefreshToken();
                final int expiresIn = ((OAuth2Credential) credential).getExpiresIn();
                final List<String> scopes = ((OAuth2Credential) credential).getScopes();
            });
        }

        @Override
        public Optional<Credential> getCredentialByUserId(String userId)
        {
            return Optional.empty();
        }
    }

    public TwitchBotCredentialManager()
    {
        super(new TwitchBotCredentialsStorage(), new AuthenticationController()
        {
            @Override
            public void startOAuth2AuthorizationCodeGrantType(OAuth2IdentityProvider oAuth2IdentityProvider, String redirectUrl, List<Object> scopes)
            {
            }
        });
    }
}
