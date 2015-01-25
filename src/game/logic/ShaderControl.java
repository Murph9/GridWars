package game.logic;
import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import javax.media.opengl.GL2;

// The shader control class.
// loads and starts/stops shaders.

/**Shader control (unsuprisingly)
 * @author Random internet user, i had the link at the bottom but its not there anymore
 *  In my defence i have changed some of it
 */
public class ShaderControl {
	
    private int vertexShaderProgram;
    private int fragmentShaderProgram;
    private int shaderprogram;
    public String[] vsrc;
    public String[] fsrc;

    // this will attach the shaders
    public void init( GL2 gl ) {
        try {
            attachShaders(gl);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // loads the shaders
    // in this example we assume that the shader is a file located in the applications JAR file.
    public String[] loadShader( String name ) {
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(name));
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append('\n');
            }
            br.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        System.err.println("Read shader: " + name); 
//        System.err.println(sb.toString());
        return new String[]{sb.toString()};
    }

    // This compiles and loads the shader to the video card.
    // if there is a problem with the source the program will exit at this point.
    private void attachShaders( GL2 gl ) throws Exception {
        vertexShaderProgram = gl.glCreateShader(GL2.GL_VERTEX_SHADER);
        fragmentShaderProgram = gl.glCreateShader(GL2.GL_FRAGMENT_SHADER);
        
        gl.glShaderSource(vertexShaderProgram, 1, vsrc, null, 0);
        gl.glCompileShader(vertexShaderProgram);
        gl.glShaderSource(fragmentShaderProgram, 1, fsrc, null, 0);
        gl.glCompileShader(fragmentShaderProgram);
        shaderprogram = gl.glCreateProgram();
        
        gl.glAttachShader(shaderprogram, vertexShaderProgram);
        gl.glAttachShader(shaderprogram, fragmentShaderProgram);
        gl.glLinkProgram(shaderprogram);
        gl.glValidateProgram(shaderprogram);
        
        IntBuffer intBuffer = IntBuffer.allocate(1);
        
        gl.glGetProgramiv(shaderprogram, GL2.GL_LINK_STATUS, intBuffer);
//        System.err.println("Linked?   " +intBuffer.get(0));
        
        if (intBuffer.get(0) != GL2.GL_TRUE) { //better than writing ' == GL2.GL_FALSE'
        	
            gl.glGetProgramiv(shaderprogram, GL2.GL_INFO_LOG_LENGTH, intBuffer);
            int size = intBuffer.get(0);
            System.err.println("Program link error: ");
            if (size > 0) {
                ByteBuffer byteBuffer = ByteBuffer.allocate(size);
                gl.glGetProgramInfoLog(shaderprogram, size, intBuffer, byteBuffer);
                for (byte b : byteBuffer.array()) {
                    System.err.print((char) b);
                }
            }
            else {
                System.out.println("Unknown");
            }
            System.exit(1);
        }
    }
    
    // this function is called when you want to activate the shader.
    // Once activated, it will be applied to anything that you draw from here on
    // until you call the dontUseShader(GL) function.
    public int useShader( GL2 gl ) {
        gl.glUseProgram(shaderprogram);
        return shaderprogram;
    }

    // when you have finished drawing everything that you want using the shaders, 
    // call this to stop further shader interactions.
    public void dontUseShader( GL2 gl ) {
        gl.glUseProgram(0);
    }
}