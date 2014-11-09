package kakao.test;
import kakao.data.Data;
import kakao.data.DataMetaInfo;
import kakao.algorithm.FM_Learn_Sgd;
import kakao.algorithm.FM_Model;
import kakao.algorithm.FM_Learn;
import kakao.algorithm.FM_Learn_Sgd_Element;
import kakao.util.Cmdline;

import org.la4j.vector.dense.BasicVector;
import java.util.ArrayList;
import java.io.FileWriter;
import java.io.BufferedWriter;
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
			if (!cmdline.hasParameter(param_regular)) {
				cmdline.setValue(param_regular, "0");
			}
			
			if (cmdline.getValue(param_method).equals("als")) {
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
                System.out.println(cmdline.getValue(param_train_file));
                Data trainData = 
                	new Data(cmdline.getValue(param_cache_size, 0),
            		!cmdline.getValue(param_method).equals("mcmc"),
            		// no original data for mcmc
            		!(cmdline.getValue(param_method).equals("sgd") || 
            		cmdline.getValue(param_method).equals("sgda")));	
                	// no transpose data for sgd, sgda
                trainData.load(cmdline.getValue(param_train_file));
                System.out.println("Loading test...");
                Data testData =
                	new Data(cmdline.getValue(param_cache_size, 0),
            		!cmdline.getValue(param_method).equals("mcmc"),
            		// no original data for mcmc
            		!(cmdline.getValue(param_method).equals("sgd") || 
            		cmdline.getValue(param_method).equals("sgda")));	
                	// no transpose data for sgd, sgda
                testData.load(cmdline.getValue(param_train_file));
                
                Data validationData = null;
                if (cmdline.hasParameter(param_val_file)) {
                	if (!cmdline.getValue(param_method).equals("sgda")) {
                		System.out.println("WARNING: Validation data is only used for SGDA. The data is ignored.");
                	} else {
                		System.out.println("Loading validation set...");
                		validationData =
                		new Data(cmdline.getValue(param_cache_size, 0),
                		!cmdline.getValue(param_method).equals("mcmc"),
                		// no original data for mcmc
                		!(cmdline.getValue(param_method).equals("sgd") || 
                		cmdline.getValue(param_method).equals("sgda")));	
                	// no transpose data for sgd, sgda
            		validationData.load(cmdline.getValue(param_val_file));
                	}
                }
                
                // TODO: (1.1.1) Load relational data
                BasicVector relation;

                // (1.2) Load meta data
                System.out.println("Loading meta data...");
                int numAllAttr = Math.max(trainData.numCols,testData.numCols);
                DataMetaInfo metaMain = new DataMetaInfo(numAllAttr);
                metaMain.loadGroupsFromFile("meta.txt");
                DataMetaInfo meta = metaMain;	// TODO: don't consider relation at this time
                
                // TODO: build the joined meta table
                

                // (2) Setup the factorization machine
                System.out.println("Setting up the factorization machine...");
                FM_Model fm = new FM_Model();
                fm.numAttr = numAllAttr;
                fm.initStdev = 0.01;	// set the number of dimensions in the factorization
                String[] dim = "1,1,8".split(",");
                fm.k0 = (Integer.parseInt(dim[0]) != 0);
                fm.k1 = (Integer.parseInt(dim[1]) != 0);
                fm.numFactor = Integer.parseInt(dim[2]);
                fm.init();
                

                // (3) Setup the learning method
                System.out.println("Setting up the learning method...");
                FM_Learn fml;
                //if (cmdline.getValue(param_method).equals("sgd")) {
                	fml = new FM_Learn_Sgd_Element();
                	((FM_Learn_Sgd)fml).numIter = cmdline.getValue(param_num_iter, 100);
                //} else if (cmdline.getValue(param_method).equals("sgda")) {
                	// fml = new FM_Learn_Sgd_Element_Adapt_Reg();
                	// ((FM_Learn_Sgd)fml).numIter = cmdline.getValue(param_num_iter, 100);
                	// ((FM_Learn_Sgd_Element_Adapt_Reg)fml).validation = validation;
                //} else if (cmdline.getValue(param_method).equals("mcmc")) {
                	// TODO: implement MCMC algorithm
                	// fml = new FM_Learn_Mcmc_Simultaneous();
                	// fml.validation = validation;
                	// fm.w_init_normal(fm.init_mean, fm.init_stdev);
				//} else {
					//System.out.println("unknown method");
                //}
                fml.fm = fm;
                fml.maxTarget = trainData.maxTarget;
                fml.minTarget = trainData.minTarget;
                fml.meta = meta;

                if (cmdline.getValue("task").equals("r")) {
                	fml.task = 0;
                } else if (cmdline.getValue("task").equals("c")) {
                	fml.task = 1;	
                	for (int i = 1; i < trainData.numRows; i++) {
                		if (trainData.target.get(i) <= 0.0) {
                                trainData.target.set(i, -1.0);
                        } else {
                                trainData.target.set(i, 1.0);
                        }
                	}
                	for (int i = 1; i < testData.numRows; i++) {
                        if (testData.target.get(i) <= 0.0) {
                                testData.target.set(i, -1.0);
                        } else {
                                testData.target.set(i, 1.0);
                        }
                	}
                	// if (validation != NULL)
                } else {
                	System.out.println("unknown task");
                }


                // (4) Init the logging
                fml.init(); 
                if (cmdline.getValue(param_method).equals("mcmc")) {
                } else {
                	// set the regularization; for standard SGD, groups are not supported
                	ArrayList<Double> reg = cmdline.getDblValues(param_regular);
                	if (reg.size() == 0) {
                		fm.reg0 = 0.0;
                		fm.regw = 0.0;
                		fm.regv = 0.0;
                	} else if (reg.size() == 1) {
                		fm.reg0 = reg.get(0);
                		fm.regw = reg.get(0);
                		fm.regv = reg.get(0);
                	} else {
                		fm.reg0 = reg.get(0);
                		fm.regw = reg.get(1);
                		fm.regv = reg.get(2);
                	}
                }
                	
                // TODO: set the learning rates (individual per layer)
                	
                // () learn
                fml.learn(trainData, testData);
                
                // () Prediction at the end (not for mcmc and als)
                if (cmdline.getValue(param_method).equals("mcmc")) {
                	System.out.println("Final\t Train=" + fml.evaluate(trainData) + "\tTest=" + fml.evaluate(testData));
                }
                
                // () Save prediction
                if (cmdline.hasParameter(param_out)) {
                	BasicVector pred;
                	pred = new BasicVector(testData.numRows+1);
                	fml.predict(testData, pred);
                	
                	BufferedWriter fData = new BufferedWriter(new FileWriter(cmdline.getValue(param_out)));
                	for (int i = 1; i < pred.length(); i++) {
                		fData.write(Double.toString(pred.get(i)));
                		fData.newLine();
                	}
                	fData.close();
                }
                
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
			e.printStackTrace();
		}
		
		
	}
}
