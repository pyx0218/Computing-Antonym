fileID = fopen('/tmp/wordMatrix.dat');
W = fread(fileID,[12204 12204],'float');

k=500;
opts = statset('Display','final','MaxIter',10);
[IDX,C,sumd,D] = kmeans(W,k,'distance','cosine','Options',opts);


load('/tmp/w2.mat')