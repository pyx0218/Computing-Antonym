

mat = [1 ];

   a  b  c  e f
a  1  0  -1 0 1
b  0  1  0  1 1 

c  0  1  1  0 0    == 2  1/2

alpha  = 0.7 
      0.7
      
      0.3
C  0  +0.5 0 +0.5 +0.5


b  0  1  0.3  0 0
b  0  1  0  0 0
b  0  1  0  0 0



w =load('/Users/homliu/Documents/workspace/Antonyms/wordMatrix.txt');
%


w = load('/tmp/hom/wordMatrix.txt') ;
save('/tmp/hom/wordMatrix.mat', 'w') ;
load('/tmp/hom/wordMatrix.mat') ;

l = load('/tmp/hom/wordList.txt2') ;


[l] = textread('/tmp/hom/wordList.txt2','%s',100000);


w =       load('/tmp/wordMatrix.txt') ;
[l] = textread('/tmp/wordList.txt2','%s',100000);

for k=1:3

    N = size(w,1);
    w2 = w;

    for j=1:N
        row = j
        new_row = w(row,:) *.5;
        %t = sum(abs(w(row,:)));
        
        for i=1:N
           if w(row,i) ~= 0 
               new_row = new_row +  w(i,:) .* (w(row,i)*.5);
           end
        end

        w2(row,:) = max( min(new_row,1), -1);
    end

    w = w2;
end


l(11478)
l(11020)
m1=norm(w(11478,:))
m2=norm(w(11020,:))
dot( w(11478,:) ,  w(11020,:) ) / ( ( norm(w(11478,:)) * norm(w(11020,:))) + 1e-100)
 
 
%$grep fluency wordList.txt 
%fluency	4391

%$grep hesitance wordList.txt 
%hesitance	5131

a = 4392 ;
b = 5132 ;
l(a)
l(b)
m1=norm(w(a,:))
m2=norm(w(b,:))
 dot( w(a,:) ,  w(b,:) ) / (norm(w(a,:) ) * norm(w(b,:)))
 
a = 4392 ;
b = 10265 ;
l(a)
l(b)
m1=norm(w(a,:))
m2=norm(w(b,:))
 dot( w(a,:) ,  w(b,:) ) / (norm(w(a,:) ) * norm(w(b,:)))
 
a = 4392 ;
b = 4440 ;
l(a)
l(b)
m1=norm(w(a,:))
m2=norm(w(b,:))
 dot( w(a,:) ,  w(b,:) ) / (norm(w(a,:) ) * norm(w(b,:)))
 
a = 4392 ;
b = 10262 ;
l(a)
l(b)
m1=norm(w(a,:))
m2=norm(w(b,:))
 dot( w(a,:) ,  w(b,:) ) / (norm(w(a,:) ) * norm(w(b,:)))
 
a = 4392 ;
b = 8792 ;
l(a)
l(b)
m1=norm(w(a,:))
m2=norm(w(b,:))
 dot( w(a,:) ,  w(b,:) ) / (norm(w(a,:) ) * norm(w(b,:)))
 
 
 
 
 
 %%%
 
for k=1:3

    N = size(w,1);
    w2 = w;

    for j=1:N
        row = j
        new_row = 0;
        row_weight = w(row,:)/sum(abs(w(row,:)));
        
        a = w' .* repmat(row_weight, N, 1);
        new_row = sum(a,2);

        w2(row,:) = max( min(new_row,1), -1);
    end

    w = w2;
end
 