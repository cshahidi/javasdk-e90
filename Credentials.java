import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;

public class Credentials {

	private ClasspathPropertiesFileCredentialsProvider myAWSCredentialFile;
	private AWSCredentials myCredentials;

	public Credentials() {
		this.myAWSCredentialFile = new ClasspathPropertiesFileCredentialsProvider();
		this.myCredentials = this.myAWSCredentialFile.getCredentials();
	}

	public AWSCredentials loadMyCredentials() {
		return this.myCredentials;
	}
}
