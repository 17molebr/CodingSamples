#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <string.h>
typedef unsigned short WORD;
typedef unsigned int DWORD;
typedef unsigned int LONG;
struct tagBITMAPFILEHEADER
       {
       WORD bfType;  //specifies the file type
       DWORD bfSize;  //specifies the size in bytes of the bitmap file
       WORD bfReserved1;  //reserved; must be 0
       WORD bfReserved2;  //reserved; must be 0
       DWORD bfOffBits;  //species the offset in bytes from the bitmapfileheader to the bitmap bits
};
struct tagBITMAPINFOHEADER
{
    DWORD biSize;  //specifies the number of bytes required by the struct
    LONG biWidth;  //specifies width in pixels
    LONG biHeight;  //species height in pixels
    WORD biPlanes; //specifies the number of color planes, must be 1
    WORD biBitCount; //specifies the number of bit per pixel 
    DWORD biCompression;//spcifies the type of compression
    DWORD biSizeImage; //size of image in bytes
    LONG biXPelsPerMeter; //number of pixels per meter in x axis 
    LONG biYPelsPerMeter; //number of pixels per meter in y axis 
    DWORD biClrUsed; //number of colors used by th ebitmap
    DWORD biClrImportant; //number of colors that are important };
};
void fillheader(FILE *file, struct tagBITMAPFILEHEADER *fh);//a function to readin a header structure
void fillinfoheader(FILE *file, struct tagBITMAPINFOHEADER *ih);//a function to reading the infoheader
void writeheaders(FILE *file, struct tagBITMAPFILEHEADER *fh, struct tagBITMAPINFOHEADER *ih);//a fucntion to wite the headers to the output
unsigned char getColor(unsigned char* data, int width, int x, int y, int color); //a function to calculate the color
unsigned char getColorFloat(unsigned char* data, int width, int height, float x, float y, int color, float dx, float dy);//a special funciton for when images are of different sizes



int main(int acgc, char* argv[]){
   //This is a propgram that reads two bmp images and a ratio from the command line and then mixes the two images together using bilinear interpolation
   struct tagBITMAPFILEHEADER fh1;
   struct tagBITMAPINFOHEADER ih1;
   struct tagBITMAPFILEHEADER fh2;
   struct tagBITMAPINFOHEADER ih2;
   //readin and open two files from commandline
   char file1[40];
   char file2[40];
   strcpy(file1, argv[1]);
   strcpy(file2, argv[2]);
   float ratio = atof(argv[3]);
   FILE *file = fopen(file1, "rb");
   if(file == 0){
       return 0;
   }
   //reading in file 1
   fillheader(file, &fh1);
   fillinfoheader(file, &ih1);
   unsigned char* data1 = (unsigned char*)malloc(ih1.biSizeImage);
   fseek(file, fh1.bfOffBits, SEEK_SET);
   fread(data1, 1, ih1.biSizeImage, file);
   
   fclose(file);
   printf("file1 read in\n");
   //done reading file 1
   FILE *second_file = fopen(file2, "rb");
   if(file == 0){
       return 0;
   }
   //readinsecond file
   fillheader(second_file, &fh2);
   fillinfoheader(second_file, &ih2);
   unsigned char* data2 = (unsigned char*)malloc(ih2.biSizeImage);
   fseek(second_file, fh2.bfOffBits, SEEK_SET);
   fread(data2, 1, ih2.biSizeImage, file);
   fclose(second_file);
   printf("file2 read in\n");
   //second file readin
   //asssign variables for calculations
   unsigned char* data_out;
   int small_width;
   int large_width;
   float width_ratio;
   int small_height;
   int large_height;
   float height_ratio;
   unsigned char* small_data;
   unsigned char* large_data;
   //only assign larger file values based on width per specs of project
   if(ih1.biWidth>ih2.biWidth){
        data_out = (unsigned char*)malloc(ih1.biSizeImage);
        large_width = ih1.biWidth;
        small_width = ih2.biWidth;
        width_ratio = (float)small_width/(float)large_width;
        large_height = ih1.biHeight;
        small_height = ih2.biHeight;
        height_ratio = (float)small_height/(float)large_height;
        small_data = data2;
        large_data = data1;
   }else{
        data_out = (unsigned char*)malloc(ih2.biSizeImage);
        large_width = ih2.biWidth;
        small_width = ih1.biWidth;
        width_ratio = (float)small_width/(float)large_width;
        large_height = ih2.biHeight;
        small_height = ih1.biHeight;
        height_ratio = (float)small_height/(float)large_height;
        small_data = data1;
        large_data = data2;
   }
   for(int y = 0 ; y<large_height; y++){
       for(int x = 0; x<large_width; x++){
           float small_x = (float)x * (float)width_ratio;
           float small_y = (float)y * (float)height_ratio;
           float dx = small_x - (int)small_x;
           float dy = small_y - (int)small_y;
           //calulate the mixing values for the bilinear interpolation 
           int real_width = large_width*3;
           if(real_width % 4 != 0){
               //calc to catch pading if file is odd width 
                real_width = real_width + 4 - real_width%4;
           }
           unsigned char b1 = getColor(large_data, large_width, x, y, 0);
           unsigned char g1 = getColor(large_data, large_width, x, y, 1);
           unsigned char r1 = getColor(large_data, large_width, x, y, 2);

           unsigned char b2 = getColorFloat(small_data, small_width, small_height, small_x, small_y, 0, dx, dy);
           unsigned char g2 = getColorFloat(small_data, small_width, small_height, small_x, small_y, 1, dx, dy);
           unsigned char r2 = getColorFloat(small_data, small_width, small_height, small_x, small_y, 2, dx, dy);

           unsigned char final_red = r1*(1-ratio)+r2*ratio;
           unsigned char final_blue = b1*(1-ratio)+b2*ratio;
           unsigned char final_green = g1*(1-ratio)+g2*ratio;
           //getcolors and mixtogether
           data_out[x*3+y*real_width] = final_blue;
           data_out[x*3+y*real_width+1] = final_green;
           data_out[x*3+y*real_width+2] = final_red;
           //add colors to the output data array 
       }
   }
   //write output data using header of larger file
   FILE * outputFile = fopen("result.bmp", "wb");
   if(ih1.biWidth>ih2.biWidth){
       writeheaders(outputFile, &fh1, &ih1);
        fwrite(data_out, 1, ih1.biSizeImage, outputFile);
   }else{
       writeheaders(outputFile, &fh2, &ih2);
       fwrite(data_out, 1, ih2.biSizeImage, outputFile);
   }

   
   fclose(outputFile);
   



}

void fillheader(FILE *file, struct tagBITMAPFILEHEADER *fh){
    //use fread to populate structure watching for padding issues in the array
    fread(&(fh->bfType), 1, 2, file);
    fread(&(fh->bfSize), 1, 4, file);
    fread(&(fh->bfReserved1), 1, 2, file);
    fread(&(fh->bfReserved2), 1, 2, file);
    fread(&(fh->bfOffBits), 1, 4, file);
}

void fillinfoheader(FILE *file, struct tagBITMAPINFOHEADER *ih){
    //no padding isues whith this sturct
    fread(ih, 1, sizeof(struct tagBITMAPINFOHEADER), file);
}

void writeheaders(FILE *file, struct tagBITMAPFILEHEADER * fh, struct tagBITMAPINFOHEADER *ih){
    //write both to ouput file
    fwrite(&fh->bfType, 1, 2, file);
    fwrite(&fh->bfSize, 1, 4, file);
    fwrite(&fh->bfReserved1, 1, 2, file);
    fwrite(&fh->bfReserved2, 1, 2, file);
    fwrite(&fh->bfOffBits, 1, 4, file); 
    fwrite(ih, 1, sizeof(struct tagBITMAPINFOHEADER), file);
}

unsigned char getColor(unsigned char* data, int width,  int x, int y, int color){
    //calc color while watching for paddin 
    int real_width = width*3;
    if(real_width % 4 != 0){
        real_width = (real_width + 4 )- (real_width%4);
    }
    return data[x*3+y*real_width+color];
}

unsigned char getColorFloat(unsigned char* data, int width, int height, float x, float y, int color, float dx, float dy){
    //do special calc for bilinear 
    int x1 = (int)floor(x);
    int y1 = (int)floor(y);
    int x2 = (int)ceil(x);
    int y2 = (int)ceil(y);
    unsigned char left_upper = getColor(data, width,  x1, y2,color);
    unsigned char right_upper = getColor(data, width,  x2, y2,color);
    unsigned char left_lower = getColor(data, width,  x1, y1,color);
    unsigned char right_lower = getColor(data, width,  x2, y1,color);
    float left = (float)left_upper*(1-dy) + (float)left_lower *dy;
    float right = (float)right_upper*(1-dy) + (float)right_lower*dy;
    unsigned char result = left*(1-dx)+right*dx;
    return result;
}