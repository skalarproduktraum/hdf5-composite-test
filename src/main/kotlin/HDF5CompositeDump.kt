import ch.systemsx.cisd.hdf5.*
import hdf.hdf5lib.H5
import hdf.hdf5lib.HDF5Constants.*


class HDF5CompositeDump(val file: String) {
    private val reader = HDF5Factory.openForReading(file)

    fun dump(path: String) {
        val dsi = reader.getDataSetInformation(path)
        println("Dataset $path:")
        println("    Dimensions: ${dsi.dimensions.joinToString("x")}")
        println("    Size: ${dsi.size}")
        println("    Type info: ${dsi.typeInformation}")
        println("    Java Type: ${dsi.typeInformation.tryGetJavaType()}")
        println("    Rank: ${dsi.rank}")

        val type = reader.compound().getDataSetType(
            path,
            HDF5CompoundDataMap::class.java
        )

        println(type.isMappingIncomplete());
        println(type.isDiskRepresentationIncomplete());
        println(type.isMemoryRepresentationIncomplete());


        println("Type: $type")
        reader.compound().read(path, HDF5CompoundDataMap::class.java).forEach {
            println(it)
        }
    }

    fun dumpLowLevel(path: String) {
//        H5.H5Tinsert(memtype, name, offset, type)


        /*
     * Now we begin the read section of this example.  Here we assume
     * the dataset has the same name and rank, but can have any size.
     * Therefore we must allocate a new array to read in data using
     * malloc().  For simplicity, we do not rebuild memtype.
     */

        /*
         * Open file and dataset.
         */
        val file = H5.H5Fopen (file, H5F_ACC_RDONLY, H5P_DEFAULT);
        val dset = H5.H5Dopen (file, path, H5P_DEFAULT);

        /*
         * Get dataspace and allocate memory for read buffer.
         */
        val space = H5.H5Dget_space (dset);
        val dims = longArrayOf(0,0,0)
        val ndims = H5.H5Sget_simple_extent_dims (space, dims, null);
        println("ndims $dims, dims[0] = ${dims[0]}")
        val rdata = ByteArray(dims[0].toInt() * 10)
//        val rdata = (sensor_t *) malloc (dims[0] * sizeof (sensor_t));

        /*
         * Read the data.
         */
        val memtype = H5.H5Dget_type(dset);
        val status = H5.H5Dread (dset, memtype, H5S_ALL.toLong(), H5S_ALL.toLong(), H5P_DEFAULT, rdata);
        println("index: ${String(rdata.take(32).toByteArray())}")
    }

    fun close() {
        reader.close()
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            if(args.size < 2) {
                error("Program needs to be given HDF5 file name and path.")
            }

            val file = args[0]
            val path = args[1]

            println("Loading HDF5 file from ${args[0]}")

            try {
                val dump = HDF5CompositeDump(file)
                dump.dump(path)
                dump.close()
            } catch (e: Exception) {
                println("Exception occured: $e")
                e.printStackTrace()
            }
        }
    }
}

