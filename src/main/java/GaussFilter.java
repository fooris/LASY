public class GaussFilter implements Filter{

    private double sigma;
    private double[] kernel;

    GaussFilter(double sigma , int kernelSize){
        if((kernelSize & 1) == 1 ) throw new IllegalArgumentException("Kernel Size must be uneven!");
        this.sigma = sigma;
        kernel = new double[kernelSize/2+1];
        double normalizer = 0;
        for(int i = 0; i < kernelSize/2+1; i++){
            kernel[i] = pdf(i,sigma);
            normalizer += kernel[i];
        }
        for(int i = 0; i < kernelSize/2+1; i++){
            kernel[i] /=normalizer;
        }
    }

    public double[] filter(double[] arr){

        double[] res = new double[arr.length];

        //We do not filter the edges!
        for(int i = kernel.length/2; i < arr.length - kernel.length/2; i++){

            res[i] += arr[i] * kernel[0];

            for(int j = 1; j < kernel.length; j++ ){
                res[i] += arr[i+j] * kernel[j];
                res[i] += arr[i-j] * kernel[j];
            }
        }

        return res;
    }


    public static double pdf(double x, double sigma) {
        return Math.exp(-x*x / (2 * sigma * sigma )) / (Math.sqrt(2 * Math.PI) * sigma);
    }
}
