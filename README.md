# scalability_tests
Source code of the scalability tests of KalibroProcessor and KalibroConfigurarions. We used the Scalability Explorer and Rehearsal frameworks (https://github.com/choreos/choreos_v-v) for scalability testing and dynamic creation of clients, respectively.

# Dependencies
To run this suite of tests, you must have:

    1. java jdk 1.8
    2. maven 3.2.5
    
Also, you must clone the choreos_v-v (https://github.com/choreos/choreos_v-v) and the Enactment Engine (https://github.com/choreos/enactment_engine) repositories. 
The first one will provide the Rehearsal and Scalability Explorer frameworks. Rehearsal will be used for dynamic creation of clients. Scalability Explorer has flexible way to test the scalability of web services and supports the calculation of three scalability metrics: __speedup__, __degradation__ and __aggregate performance comparison__. Furthermore, it is possible to vary the workload and the probability distribution function of requests over time.
Enactment Engine is platform that automatically deploys web service compositions. Although we will not use it on our scalability tests, it is a powerful platform for using cloud elasticity.

Scalability Explorer depends on Rehearsal and Enactment Engine. You have to install them first.

### Enactment Engine Installation
Clone the repository. Then, on its root directory run:

    mvn install
    
### Rehearsal and Scalability Explorer Installation
Clone the repository, go to the rehearsal directory and run:

    mvn install
    
Now, go to the scalability_explorer directory and run:

    mvn install

If the tests fail, you can run maven with the following option

    mvn install -Dmaven.test.skip=true
    
It will compile the tests but not run it.
