package kakao.test;
import kakao.data.Data;
import kakao.data.DataMetaInfo;
import kakao.algorithm.FM_Learn_Sgd;
import kakao.algorithm.FM_Model;
import kakao.algorithm.FM_Learn;
import kakao.algorithm.FM_Learn_Sgd_Element;
import kakao.util.Cmdline;

import java.io.IOException;

public class Test {
	public static void main(String[] args) throws IOException {
		try {
			int argc = args.length;
			Cmdline cmdline = new Cmdline(argc, args);
			System.out.println("----------------------------------------------------------------------------");
			System.out.println("LibFM");
			System.out.println("  Version: 1.4.2");
			System.out.println("  Author: Kilho Kim, catharsistk@gmail.com");
			System.out.println("  Original Author: Steffen Rendle, srendle@libfm.org");
			System.out.println("  WWW:  http://imlab.snu.ac.kr");
			System.out.println("This program comes with ABSOLUTELY NO WARRANTY; for details see license.txt.");
			System.out.println("This is free software, and you are welcome to redistribute it under certain");
			System.out.println("conditions; for details see license.txt.");
			System.out.println("----------------------------------------------------------------------------");
			
			String param_task = cmdline.registerParameter("task", "r=regression, c=binary classification [MANDATORY]");
			String param_meta_file = cmdline.registerParameter("meta", "filename for meta information about data set");
			String param_train_file = cmdline.registerParameter("train", "filename for training data [MANDATORY]");
			String param_test_file = cmdline.registerParameter("test", "filename for test data [MANDATORY]");
			String param_val_file = cmdline.registerParameter("validation", "filename for validation data (only for SGDA)");
			String param_out = cmdline.registerParameter("out", "filename for output");

			String param_dim = cmdline.registerParameter("dim","'k0,k1,k2': k0=use bias, k1= use 1-way interactions, k2=dim of 2-way interactions; default=1,1,8");
			String param_regular = cmdline.registerParameter("regular","'r0,r1,r2' for SGD and ALS: r0=bias regularization, r1=1-way regularization, r2=2-way regularization");
			String param_init_stdev = cmdline.registerParameter("init_stdev", "stdev for initialization of 2-way factors; default=0.1");
			String param_num_iter = cmdline.registerParameter("iter", "number of iterations; default=100");
			String param_learn_rate = cmdline.registerParameter("learn_rate", "learn_rate for SGD; default=0.1");
			
			String param_method = cmdline.registerParameter("method","learning method (SGD, SGDA, ALS, MCMC); default=MCMC");
			
			String param_verbosity = cmdline.registerParameter("verbosity","how much infos to print; default=0");
			String param_r_log = cmdline.registerParameter("rlog", "write measurements within iterations to a file; default=''");
			String param_help = cmdline.registerParameter("help","this screen");
			
			String param_relation = cmdline.registerParameter("relation", "BS: filenames for the relations, default=''");
			
			String param_cache_size = cmdline.registerParameter("cache_size", "cache size for data storage (only applicable if data is in binary format), default=infty");
			
			String param_do_sampling = "do_sampling";
			String param_do_multilevel = "do_multilevel";
			String param_num_eval_cases = "num_eval_cases";
			
			if (cmdline.hasParameter(param_help) || argc == 1) {
				cmdline.print_help();
			}
			cmdline.checkParameters();
			
			if (!cmdline.hasParameter(param_method)) {
				cmdline.setValue(param_method, "mcmc");
			}
			if (!cmdline.hasParameter(param_init_stdev)) {
				cmdline.setValue(param_init_stdev, "0.1");
			}
			if (!cmdline.hasParameter(param_dim)) {
				cmdline.setValue(param_dim, "1,1,8");
			}
			
			if (!cmdline.getValue(param_method).equals("als")) {
			// als is an mcmc without sampling and hyperparameter inference
				cmdline.setValue(param_method, "mcmc");
				if (!cmdline.hasParameter(param_do_sampling)) {
					cmdline.setValue(param_do_sampling, "0");
				}
				if (!cmdline.hasParameter(param_do_multilevel)) {
					cmdline.setValue(param_do_multilevel, "0");
				}
			}

                // (1) Load the data
                System.out.println("Loading train...");
                Data trainData = new Data();
                trainData.load("train.libsvm");
                System.out.println("Loading test...");
                Data testData = new Data();
                testData.load("test.libsvm");
                
                // (1.2) Load meta data
                System.out.println("Loading meta data...");
                int numAllAttr = Math.max(trainData.numCols,testData.numCols);
                DataMetaInfo metaMain = new DataMetaInfo(numAllAttr);
                metaMain.loadGroupsFromFile("meta.txt");
                DataMetaInfo meta = metaMain;	// TODO: don't consider relation at this time
                
                // build the joined meta table
                
                // (2) Setup the factorization machine
                FM_Model fm = new FM_Model();
                fm.numAttr = numAllAttr;
                fm.initStdev = 0.01;	// set the number of dimensions in the factorization
                String[] dim = "1,1,8".split(",");
                fm.k0 = (Integer.parseInt(dim[0]) != 0);
                fm.k1 = (Integer.parseInt(dim[1]) != 0);
                fm.numFactor = Integer.parseInt(dim[2]);
                fm.init();
                
                // (3) Setup the learning method
                FM_Learn_Sgd fml = new FM_Learn_Sgd_Element();
                fml.numIter = 100;
                // fml.validation = validation;
                fml.fm = fm;
                fml.maxTarget = trainData.maxTarget;
                fml.minTarget = trainData.minTarget;
                fml.meta = meta;

                fml.task = 1;	// set as classification problem at this time
                for (int i = 1; i < trainData.numRows; i++) {
                        if (trainData.target.get(i) <= 0.0) {
                                trainData.target.set(i, -1.0);
                        } else {
                                trainData.target.set(i, 1.0);
                        }
                }
                for (int i = 1; i < trainData.numRows; i++) {
                        if (testData.target.get(i) <= 0.0) {
                                testData.target.set(i, -1.0);
                        } else {
                                testData.target.set(i, 1.0);
                        }
                }

                // validation

                // (4) Init the logging
                
                
                
                
                
                
                
                
                
                
                
                
                
                /*
                BasicVector vector = new BasicVector(6);
                
                vector.set(1, 3);
                vector.set(2, 2);
                vector.set(3, 1);
                vector.set(4, 4);
                vector.set(5, 5);
                //vector.set(5, 6);
                
                for (int i = 0; i < vector.length(); i++) {
                        System.out.println(vector.get(i));
                }
                System.out.println("");
                
                vector = (BasicVector)vector.resize(3);

                for (int i = 0; i < vector.length(); i++) {
                        System.out.println(vector.get(i));
                }
                System.out.println("");
                
                
                vector = (BasicVector)vector.resize(7);

                for (int i = 0; i < vector.length(); i++) {
                        System.out.println(vector.get(i));
                }
                */

                /*
                Data test = new Data();
                

                try {
                        test.load("test.libsvm");
                        System.out.println("\nData:");
                        for (int i = 1; i <= test.numRows; i++) {
                                for (int j = 1; j <= test.numCols; j++) {
                                        double currFeatureValue = test.data.get(i,j);
                                        if (currFeatureValue > 0) {
                                                System.out.print("(" + j + "," + currFeatureValue + ")");
                                        }
                                }
                                System.out.print("\n");
                        }
                        
                        System.out.println("\nTarget:");
                        System.out.print("(");
                        for (int i = 1; i < test.numRows; i++) {
                                System.out.print(test.target.get(i) + ",");
                        }
                        System.out.print(")");
                } catch (IOException e) {
                        System.out.println(e);
                }
                */
        
		
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		
	}
}
