

   a  b  c  e f
a  1  0  -1 0 1
b  0  1  0  0 0 
c  0  1  01  0 0
b  0  1  0.3  0 0
b  0  1  0  0 0
b  0  1  0  0 0



w =load('/Users/homliu/Documents/workspace/Antonyms/wordMatrix.txt');
%load('wordMatrix.mat')

for k=1:3

    N = size(w,1);
    w2 = w;

    for j=1:N
        row = j
        new_row = 0;
        t = sum(abs(w(row,:)));
        
        for i=1:N
           if w(row,i) ~= 0 
               new_row = new_row +  w(:,i)' .* (w(row,i) / t);
           end
        end

        w2(row,:) = max( min(new_row,1), -1);
    end

    w = w2;
end


dot( w(11477,:) ,  w(11019,:) ) / (norm(w(11477,:) ) * norm(w(11019,:)))
 
 
%$grep fluency wordList.txt 
%fluency	4391

%$grep hesitance wordList.txt 
%hesitance	5131

a = 4391 ;
b = 5131 ;
 dot( w(a,:) ,  w(b,:) ) / (norm(w(a,:) ) * norm(w(b,:)))
 
a = 4391 ;
b = 10264 ;
 dot( w(a,:) ,  w(b,:) ) / (norm(w(a,:) ) * norm(w(b,:)))
 
a = 4391 ;
b = 4439 ;
 dot( w(a,:) ,  w(b,:) ) / (norm(w(a,:) ) * norm(w(b,:)))
 
a = 4391 ;
b = 10261 ;
 dot( w(a,:) ,  w(b,:) ) / (norm(w(a,:) ) * norm(w(b,:)))
 
a = 4391 ;
b = 8791 ;
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
 