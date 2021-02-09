clear all; close all;
oimg = imread('35_1048.jpg');
[newimg, map] = rgb2ind(oimg, 16);
colormap(map)
hsvmap = rgb2hsv(map)

[rowCount,colcount,junk] = size(hsvmap);
h = hsvmap(:,1);
s = hsvmap(:,2);
v = hsvmap(:,3);


edges = [0, 0.053, 0.1083, 0.1805, 0.4444, 0.5277, 0.7138, 0.8055, 0.8888, 1]
colors = discretize(h,edges, 'categorical', {'red' 'orange' 'yellow' 'green' 'cyan' 'blue' 'purple' 'magenta' 'red'});

%colors = zeros(1, length(hsvmap));
%colors(or(h < 0.25, h > 0.95)) = 'red';
%colors(h >= 0.25 & h < 0.35) = 'orange';
%colors(h >= 0.25 && h < 0.95) = 'red';
%colors(h >= 0.25 && h < 0.95) = 'red';

colors = addcats(colors, {'black' 'white' 'brown' 'grey' }, 'After', 'orange');

colors(v < 0.25) = 'black';
colors(s < 0.05 & v > 0.95) = 'white';
colors((h >= 0.53 & h < 0.1083) & (v > .25 & v < .65)) = 'brown';
colors(isundefined(colors)) = 'grey';

finalRow = 160;
finalColumn = 160;
[nrows,ncols,junk] = size(newimg)
chunkRow = ceil(nrows / finalRow);
chunkColumn = ceil(ncols / finalColumn);
resimg = mat2tiles(newimg, chunkRow, chunkColumn);

resimg2 = cellfun(@(x) mode(x, 'all'), resimg,'UniformOutput',false);

finalcatimg = categorical(cell2mat(resimg2),[0 1 2 3 4 5 6 7 8 9 10 11 12 13 14 15], cellstr(colors));
finalnumimg = categorical(finalcatimg,{'red' 'orange' 'yellow' 'green' 'cyan' 'blue' 'purple' 'magenta' 'black' 'white' 'brown' 'grey'},{'1' '2' '3' '4' '5' '6' '7' '8' '9' '10' '11' '12'});
%now take those averaged colors and then divide them up into sections and find the most dominant color.
%B = reshape(newimg,25, [])
%resimg = imresize(newimg, [320 NaN]);
%I want to create the chunks based on the number of pixels the final output
%is set to be. So I need a variable for finalRow, finalColumn. I need to
%divide newimg rows and columns by finalRow and finalColumn to get how big
%each chunk is supposed to be, which means I need variables chunkRow and
%chunkColumn.
imagesc(newimg)
writecell(resimg2, 'output.csv')

