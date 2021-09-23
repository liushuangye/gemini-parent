<template>
  <div class="usernamage">
    <div class="usernamage-wrap">
      <div class="div-a">
        <el-tree
          ref="tree"
          :data="data"
          :props="defaultProps"
          class="filter-tree"
          node-key="busId"
          check-strictly
          show-checkbox
          check-on-click-node
          :default-expanded-keys="['ALL']"
          :default-checked-keys="defaultChecked"
          @check-change="handleCheckChange"
        />
      </div>
      <div class="div-b">
        <div class="search">
          <el-form :inline="true">
            <el-form-item label-width="1px">
              <el-button type="primary" size="mini" @click="saveDate()"> {{ $t('sysparam.save') }} </el-button>
              <el-button type="primary" size="mini" @click="showDialogVisible()"> {{ $t('sysparam.import') }} </el-button>
              <el-button type="primary" size="mini" :disabled="btnDisabled" @click="downloadCode()"> {{ $t('sysparam.export') }} </el-button>
            </el-form-item>
          </el-form>
        </div>
        <div class="line" />
        <div class="tabledata">
          <el-table
            ref="multipleTable"
            v-loading="listLoading"
            :data="list"
            element-loading-text="Loading"
            style="width: 100%;"
            :max-height="tableHeight"
            border
            highlight-current-row
            :row-key="getRowKeys"
            :header-cell-style="{background:'#E5E9EF',color:'#606266'}"
            @selection-change="handleSelectionChange"
          >
            <el-table-column fixed="left" align="center" type="selection" :reserve-selection="true" width="55" />
            <el-table-column fixed="left" align="center" :label="$t('sysparam.codeId')" width="200" min-width="100">
              <template v-slot="scope">
                <div v-if="!multipleSelection.every(item => item.codeId != scope.row.codeId)">
                  <el-input
                    v-model="scope.row.codeId"
                    type="textarea"
                    :autosize="{ minRows: 1, maxRows: 5}"
                  />
                </div>
                <div v-if="multipleSelection.every(item => item.codeId != scope.row.codeId)" style="text-align:left;max-height:110px;">
                  {{ scope.row.codeId }}
                </div>
              </template>
            </el-table-column>
            <el-table-column align="center" :label="$t('sysparam.codeName')" width="200" min-width="100">
              <template v-slot="scope">
                <div v-if="!multipleSelection.every(item => item.codeId != scope.row.codeId)">
                  <el-input
                    v-model="scope.row.codeName"
                    type="textarea"
                    :autosize="{ minRows: 1, maxRows: 5}"
                  />
                </div>
                <div v-if="multipleSelection.every(item => item.codeId != scope.row.codeId)" style="text-align:left;max-height:110px;">
                  {{ scope.row.codeName }}
                </div>
              </template>
            </el-table-column>
            <el-table-column align="center" :label="$t('sysparam.codeRnm')" width="200" min-width="100">
              <template v-slot="scope">
                <div v-if="!multipleSelection.every(item => item.codeId != scope.row.codeId)">
                  <el-input
                    v-model="scope.row.codeRnm"
                    type="textarea"
                    :autosize="{ minRows: 1, maxRows: 5}"
                  />
                </div>
                <div v-if="multipleSelection.every(item => item.codeId != scope.row.codeId)" style="text-align:left;max-height:110px;">
                  {{ scope.row.codeRnm }}
                </div>
              </template>
            </el-table-column>
            <el-table-column align="center" :label="$t('sysparam.codeValue1')" width="200" min-width="100">
              <template v-slot="scope">
                <div v-if="!multipleSelection.every(item => item.codeId != scope.row.codeId)">
                  <el-input
                    v-model="scope.row.codeValue1"
                    type="textarea"
                    :autosize="{ minRows: 1, maxRows: 5}"
                  />
                </div>
                <div v-if="multipleSelection.every(item => item.codeId != scope.row.codeId)" style="text-align:left;max-height:110px;">
                  {{ scope.row.codeValue1 }}
                </div>
              </template>
            </el-table-column>
            <el-table-column align="center" :label="$t('sysparam.codeValue2')" width="200" min-width="100">
              <template v-slot="scope">
                <div v-if="!multipleSelection.every(item => item.codeId != scope.row.codeId)">
                  <el-input
                    v-model="scope.row.codeValue2"
                    type="textarea"
                    :autosize="{ minRows: 1, maxRows: 5}"
                  />
                </div>
                <div v-if="multipleSelection.every(item => item.codeId != scope.row.codeId)" style="text-align:left;max-height:110px;">
                  {{ scope.row.codeValue2 }}
                </div>
              </template>
            </el-table-column>
            <el-table-column align="center" :label="$t('sysparam.codeValue3')" width="200" min-width="100">
              <template v-slot="scope">
                <div v-if="!multipleSelection.every(item => item.codeId != scope.row.codeId)">
                  <el-input
                    v-model="scope.row.codeValue3"
                    type="textarea"
                    :autosize="{ minRows: 1, maxRows: 5}"
                  />
                </div>
                <div v-if="multipleSelection.every(item => item.codeId != scope.row.codeId)" style="text-align:left;max-height:110px;">
                  {{ scope.row.codeValue3 }}
                </div>
              </template>
            </el-table-column>
            <el-table-column align="center" :label="$t('sysparam.codeValue4')" width="200" min-width="100">
              <template v-slot="scope">
                <div v-if="!multipleSelection.every(item => item.codeId != scope.row.codeId)">
                  <el-input
                    v-model="scope.row.codeValue4"
                    type="textarea"
                    :autosize="{ minRows: 1, maxRows: 5}"
                  />
                </div>
                <div v-if="multipleSelection.every(item => item.codeId != scope.row.codeId)" style="text-align:left;max-height:110px;">
                  {{ scope.row.codeValue4 }}
                </div>
              </template>
            </el-table-column>
            <el-table-column align="center" :label="$t('sysparam.codeValue5')" width="200" min-width="100">
              <template v-slot="scope">
                <div v-if="!multipleSelection.every(item => item.codeId != scope.row.codeId)">
                  <el-input
                    v-model="scope.row.codeValue5"
                    type="textarea"
                    :autosize="{ minRows: 1, maxRows: 5}"
                  />
                </div>
                <div v-if="multipleSelection.every(item => item.codeId != scope.row.codeId)" style="text-align:left;max-height:110px;">
                  {{ scope.row.codeValue5 }}
                </div>
              </template>
            </el-table-column>
            <el-table-column align="center" :label="$t('sysparam.codeRemark')" width="200" min-width="100">
              <template v-slot="scope">
                <div v-if="!multipleSelection.every(item => item.codeId != scope.row.codeId)">
                  <el-input
                    v-model="scope.row.codeRemark"
                    type="textarea"
                    :autosize="{ minRows: 1, maxRows: 5}"
                  />
                </div>
                <div v-if="multipleSelection.every(item => item.codeId != scope.row.codeId)" style="text-align:left;max-height:110px;">
                  {{ scope.row.codeRemark }}
                </div>
              </template>
            </el-table-column>
            <el-table-column align="center" :label="$t('sysparam.preparation1')" width="200" min-width="100">
              <template v-slot="scope">
                <div v-if="!multipleSelection.every(item => item.codeId != scope.row.codeId)">
                  <el-input
                    v-model="scope.row.preparation1"
                    type="textarea"
                    :autosize="{ minRows: 1, maxRows: 5}"
                  />
                </div>
                <div v-if="multipleSelection.every(item => item.codeId != scope.row.codeId)" style="text-align:left;max-height:110px;">
                  {{ scope.row.preparation1 }}
                </div>
              </template>
            </el-table-column>
            <el-table-column align="center" :label="$t('sysparam.preparation2')" width="200" min-width="100">
              <template v-slot="scope">
                <div v-if="!multipleSelection.every(item => item.codeId != scope.row.codeId)">
                  <el-input
                    v-model="scope.row.preparation2"
                    type="textarea"
                    :autosize="{ minRows: 1, maxRows: 5}"
                  />
                </div>
                <div v-if="multipleSelection.every(item => item.codeId != scope.row.codeId)" style="text-align:left;max-height:110px;">
                  {{ scope.row.preparation2 }}
                </div>
              </template>
            </el-table-column>
            <el-table-column align="center" :label="$t('sysparam.preparation3')" width="200" min-width="100">
              <template v-slot="scope">
                <div v-if="!multipleSelection.every(item => item.codeId != scope.row.codeId)">
                  <el-input
                    v-model="scope.row.preparation3"
                    type="textarea"
                    :autosize="{ minRows: 1, maxRows: 5}"
                  />
                </div>
                <div v-if="multipleSelection.every(item => item.codeId != scope.row.codeId)" style="text-align:left;max-height:110px;">
                  {{ scope.row.preparation3 }}
                </div>
              </template>
            </el-table-column>
            <el-table-column align="center" :label="$t('sysparam.preparation4')" width="200" min-width="100">
              <template v-slot="scope">
                <div v-if="!multipleSelection.every(item => item.codeId != scope.row.codeId)">
                  <el-input
                    v-model="scope.row.preparation4"
                    type="textarea"
                    :autosize="{ minRows: 1, maxRows: 5}"
                  />
                </div>
                <div v-if="multipleSelection.every(item => item.codeId != scope.row.codeId)" style="text-align:left;max-height:110px;">
                  {{ scope.row.preparation4 }}
                </div>
              </template>
            </el-table-column>
            <el-table-column align="center" :label="$t('sysparam.preparation5')" width="200" min-width="100">
              <template v-slot="scope">
                <div v-if="!multipleSelection.every(item => item.codeId != scope.row.codeId)">
                  <el-input
                    v-model="scope.row.preparation5"
                    type="textarea"
                    :autosize="{ minRows: 1, maxRows: 5}"
                  />
                </div>
                <div v-if="multipleSelection.every(item => item.codeId != scope.row.codeId)" style="text-align:left;max-height:110px;">
                  {{ scope.row.preparation5 }}
                </div>
              </template>
            </el-table-column>
            <el-table-column align="center" :label="$t('sysparam.preparation6')" width="200" min-width="100">
              <template v-slot="scope">
                <div v-if="!multipleSelection.every(item => item.codeId != scope.row.codeId)">
                  <el-input
                    v-model="scope.row.preparation6"
                    type="textarea"
                    :autosize="{ minRows: 1, maxRows: 5}"
                  />
                </div>
                <div v-if="multipleSelection.every(item => item.codeId != scope.row.codeId)" style="text-align:left;max-height:110px;">
                  {{ scope.row.preparation6 }}
                </div>
              </template>
            </el-table-column>
            <el-table-column align="center" :label="$t('sysparam.preparation7')" width="200" min-width="100">
              <template v-slot="scope">
                <div v-if="!multipleSelection.every(item => item.codeId != scope.row.codeId)">
                  <el-input
                    v-model="scope.row.preparation7"
                    type="textarea"
                    :autosize="{ minRows: 1, maxRows: 5}"
                  />
                </div>
                <div v-if="multipleSelection.every(item => item.codeId != scope.row.codeId)" style="text-align:left;max-height:110px;">
                  {{ scope.row.preparation7 }}
                </div>
              </template>
            </el-table-column>
            <el-table-column align="center" :label="$t('sysparam.preparation8')" width="200" min-width="100">
              <template v-slot="scope">
                <div v-if="!multipleSelection.every(item => item.codeId != scope.row.codeId)">
                  <el-input
                    v-model="scope.row.preparation8"
                    type="textarea"
                    :autosize="{ minRows: 1, maxRows: 5}"
                  />
                </div>
                <div v-if="multipleSelection.every(item => item.codeId != scope.row.codeId)" style="text-align:left;max-height:110px;">
                  {{ scope.row.preparation8 }}
                </div>
              </template>
            </el-table-column>
            <el-table-column align="center" :label="$t('sysparam.preparation9')" width="200" min-width="100">
              <template v-slot="scope">
                <div v-if="!multipleSelection.every(item => item.codeId != scope.row.codeId)">
                  <el-input
                    v-model="scope.row.preparation9"
                    type="textarea"
                    :autosize="{ minRows: 1, maxRows: 5}"
                  />
                </div>
                <div v-if="multipleSelection.every(item => item.codeId != scope.row.codeId)" style="text-align:left;max-height:110px;">
                  {{ scope.row.preparation9 }}
                </div>
              </template>
            </el-table-column>
            <el-table-column align="center" :label="$t('sysparam.preparation10')" width="200" min-width="100">
              <template v-slot="scope">
                <div v-if="!multipleSelection.every(item => item.codeId != scope.row.codeId)">
                  <el-input
                    v-model="scope.row.preparation10"
                    type="textarea"
                    :autosize="{ minRows: 1, maxRows: 5}"
                  />
                </div>
                <div v-if="multipleSelection.every(item => item.codeId != scope.row.codeId)" style="text-align:left;max-height:110px;">
                  {{ scope.row.preparation10 }}
                </div>
              </template>
            </el-table-column>
            <el-table-column fixed="right" align="center" :label="$t('common.operat')" width="100" min-width="100">
              <template v-slot="scope">
                <el-button type="text" style="margin-left: 10px;color:#F00;" size="mini" @click="deleteDate(scope.row)"> {{ $t('common.delete') }} </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
        <div class="page">
          <el-pagination
            background
            :current-page="codeForm.currentPage"
            :page-sizes="[10, 20, 30, 40]"
            :page-size="codeForm.pagesize"
            layout="total, sizes, prev, pager, next"
            :total="listSize"
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
          />
        </div>
      </div>
      <el-dialog
        :visible.sync="dialogVisible"
        :title="$t('sysparam.importXLS')"
        width="30%"
      >
        <el-form label-width="25%">
          <el-form-item :label="$t('sysparam.selectFile')">
            <div>
              <el-input v-model="fileName" disabled size="mini" style="width: 80%; display: inline-block;" /> &nbsp;&nbsp;
              <el-button size="mini" @click="fileSelect()">...</el-button>
              <input ref="fileagain" type="file" style="display: none;" class="upfile" name="file" accept=".xls" @change="changeInput()">
            </div>
          </el-form-item>
        </el-form>
        <div slot="footer" style="text-align:right">
          <el-button type="primary" size="mini" @click="uploadFile">{{ $t('common.btnConfirm') }}</el-button>
          <el-button size="mini" @click="dialogVisible = false">{{ $t('common.btnCancel') }}</el-button>
        </div>
      </el-dialog>
    </div>
  </div>
</template>

<script>
import { getTree, getTCode, save, deleteCode, outPutXLS, importXLS } from '@/api/sysparam.js'
export default {

  data() {
    return {
      data: [],
      defaultChecked: [],
      defaultProps: {
        children: 'children',
        label: 'label'
      },
      codeForm: {
        busId: '',
        currentPage: 1,
        pagesize: 20
      },
      listLoading: true,
      list: [],
      listSize: 0,
      tableHeight: 0,
      multipleSelection: [],
      dialogVisible: false,
      fileName: '',
      btnDisabled: false
    }
  },

  watch: {
  },
  created() {
    const screenHeight = document.documentElement.clientHeight - 200 + 'px'
    this.$nextTick(() => {
      this.tableHeight = screenHeight
    })
  },
  mounted() {
    this.getTreeData()
    const that = this
    window.onresize = () => {
      return (() => {
        const screenHeight = document.documentElement.clientHeight - 200 + 'px'
        that.tableHeight = screenHeight
      })()
    }
  },

  methods: {
    // 树选中数据
    handleCheckChange(data, checked) {
      if (checked) {
        // 选中数据 单选
        this.$refs.tree.setCheckedNodes([data])
        this.codeForm.busId = data.busId
        // 重置翻页组件数据
        this.codeForm.currentPage = 1
        this.codeForm.pagesize = 20
        // 取消选中
        this.$refs.multipleTable.clearSelection()
        this.getTCodeData()
      } else {
        // 取消选中
        // 最后一个不允许取消
        if (this.$refs.tree.getCheckedKeys().length === 0) {
          this.$refs.tree.setCheckedNodes([data])
        }
      }
    },

    // 获取树
    getTreeData() {
      // 调用获取树接口
      getTree().then(res => {
        if (res.data && res.code === 0) {
          this.data = res.data
          // 设置默认选中
          var busidList = []
          busidList.push(res.data[0].children[0].busId)
          this.defaultChecked = busidList
          this.codeForm.busId = res.data[0].children[0].busId
          this.getTCodeData()
        }
      })
    },

    // 获取系统参数配置列表
    getTCodeData() {
      this.listLoading = true
      // 调用获取参数配置接口
      getTCode(this.codeForm).then(res => {
        if (res.data && res.code === 0) {
          this.list = res.data.records
          this.listSize = res.data.total
        }
      }).finally(() => {
        this.listLoading = false
      })
    },

    // 更新数据
    saveDate() {
      // 是否选中数据？
      if (this.multipleSelection.length <= 0) {
        this.$message({
          type: 'warning',
          message: this.$t('sysparam.editNull'),
          duration: 1000
        })
        return
      }
      // 数据编号 数据名称 check
      for (var i = 0; i < this.multipleSelection.length; i++) {
        if (this.multipleSelection[i].codeId === '') {
          this.$message({
            type: 'warning',
            message: this.$t('sysparam.codeIdNull'),
            duration: 1000
          })
          return
        }
        if (this.multipleSelection[i].codeName === '') {
          this.$message({
            type: 'warning',
            message: this.$t('sysparam.codeNameNull'),
            duration: 1000
          })
          return
        }
      }

      // 是否更新？
      this.$confirm(this.$t('sysparam.changeConfirm'), this.$t('common.prompt'), {
        // 确认
        confirmButtonText: this.$t('common.btnConfirm'),
        // 取消
        cancelButtonText: this.$t('common.btnCancel'),
        cancelButtonClass: 'bhh-btn-custom-cancel'
      }).then(async() => {
        this.listLoading = true
        // 调用更新接口
        save(this.multipleSelection).then(res => {
          if (res.code === 0) {
            // 重置翻页组件数据
            this.codeForm.currentPage = 1
            this.codeForm.pagesize = 20
            // 取消选中
            this.$refs.multipleTable.clearSelection()
            this.getTCodeData()
            this.$message({
              type: 'success',
              message: this.$t('sysparam.editSuccess'),
              duration: 1000
            })
          }
        }).finally(() => {
          this.listLoading = false
        })
      })
    },

    // 删除数据
    deleteDate(date) {
      // 是否删除？
      this.$confirm(this.$t('common.delConfirm'), this.$t('common.prompt'), {
        // 确认
        confirmButtonText: this.$t('common.btnConfirm'),
        // 取消
        cancelButtonText: this.$t('common.btnCancel'),
        cancelButtonClass: 'bhh-btn-custom-cancel'
      }).then(async() => {
        this.listLoading = true
        date.deleteFlg = 1
        // 调用删除接口
        deleteCode(date).then(res => {
          if (res.code === 0) {
            // 重置翻页组件数据
            this.codeForm.currentPage = 1
            this.codeForm.pagesize = 20
            // 取消选中
            this.$refs.multipleTable.clearSelection()
            this.getTCodeData()
            this.$message({
              type: 'success',
              message: this.$t('common.deleteSuccess'),
              duration: 1000
            })
          }
        }).finally(() => {
          this.listLoading = false
        })
      })
    },

    // 文件导出
    downloadCode() {
      this.listLoading = true
      this.btnDisabled = true
      // 调用文件导出接口
      outPutXLS(this.codeForm).then(res => {
        if (res !== undefined) {
          const temp = localStorage.getItem('content-disposition').split(';')[1].split('filename=')[1]
          let filename = decodeURIComponent(temp)
          filename = filename.replace(/\"/g, '')
          const blob = new Blob([res], { type: 'application/vnd.ms-excel;charset=UTF-8' })
          const a = document.createElement('a')
          a.download = filename
          a.href = window.URL.createObjectURL(blob)
          a.click()
          window.URL.revokeObjectURL(a.href) // 释放URL 对象
        }
      }).finally(() => {
        this.listLoading = false
        this.btnDisabled = false
      })
    },

    // 文件
    uploadFile() {
      // 是否选中文件
      if (this.$refs.fileagain.files.length === 0) {
        this.$message({
          type: 'warning',
          message: this.$t('sysparam.fileNull'),
          duration: 1000
        })
        return
      }
      // 是否上传？
      this.$confirm(this.$t('sysparam.uplodeConfirm'), this.$t('common.prompt'), {
        // 确认
        confirmButtonText: this.$t('common.btnConfirm'),
        // 取消
        cancelButtonText: this.$t('common.btnCancel'),
        cancelButtonClass: 'bhh-btn-custom-cancel'
      }).then(async() => {
        this.listLoading = true
        const formData = new FormData()
        formData.append('file', this.$refs.fileagain.files[0])
        formData.append('fileName', this.fileName)
        importXLS(formData).then(res => {
          if (res.code === 0) {
            // 重置翻页组件数据
            this.codeForm.currentPage = 1
            this.codeForm.pagesize = 20
            // 取消选中
            this.$refs.multipleTable.clearSelection()
            this.getTCodeData()
            this.$message({
              type: 'success',
              message: this.$t('sysparam.uplodeSuccess'),
              duration: 1000
            })
          }
        }).finally(() => {
          this.listLoading = false
          this.dialogVisible = false
        })
      })
    },

    // table选中数据
    handleSelectionChange(val) {
      this.multipleSelection = val
    },

    // 翻页保留已选择
    getRowKeys(row) {
      return row.codeId
    },

    // 打开文件上传页面
    showDialogVisible() {
      this.fileName = ''
      if (this.$refs.fileagain !== undefined) {
        this.$refs.fileagain.value = ''
      }
      this.dialogVisible = true
    },

    // 打开选择文件页面
    fileSelect() {
      this.$refs.fileagain.click()
    },

    // 选择文件显示文件名
    changeInput() {
      const file = this.$refs.fileagain.files[0]
      this.fileName = file.name
    },

    // 修改每页展示多少条数据
    handleSizeChange(size) {
      this.codeForm.pagesize = size
      this.getTCodeData()
    },

    // 翻页
    handleCurrentChange(page) {
      this.codeForm.currentPage = page
      this.getTCodeData()
    }

  }
}
</script>

<style scoped>
.grid-content {border-radius: 5px;min-height: 200px;min-width: 160px;}
.usernamage{text-align: left;width: 98%;height: 100%;margin: 0 auto;overflow: hidden;font-size: 15px;}
.usernamage-wrap{height: 100%;background: #ffffff;border-radius: 6px;overflow: hidden;}
.search{margin: 0 !important;padding: 5px 0;display: flex;align-items: center;background-color: #fff;}
.search .el-form .el-form-item{margin: 0 0 0 10px !important;}
.search .el-form .el-form-item /deep/ .el-form-item__label{
    height: 30px;
    line-height: 30px;
    font-weight:normal !important;
}
.search .el-form .el-form-item /deep/ .el-form-item__content{
    height: 30px;
    line-height: 30px;
}
@media screen and (max-width: 1366px) {
  .search .el-form .el-form-item /deep/ .el-form-item__label{
      height: 23px;
      line-height: 23px;
  }
  .search .el-form .el-form-item /deep/ .el-form-item__content{
      height: 23px;
      line-height: 23px;
  }
  .search .el-form .el-form-item /deep/ .el-form-item__content .el-input__icon{
      line-height: 24px;
  }
  .search .el-form .el-form-item /deep/ .el-form-item__content .el-input__inner{
      height: 23px !important;
      line-height: 24px !important;
      vertical-align: middle;
  }
  .search .el-form .el-form-item /deep/ .el-form-item__content button{
      height: 23px !important;
      line-height: 23px !important;
      padding: 0 10px !important;
  }
  .search .el-form .el-form-item /deep/ .el-form-item__content button+button{
    margin-left: 7px;
  }
}
.page {background:#fff;text-align: right;text-indent: initial;margin-top: 5px;}
.usernamage /deep/ .el-dialog__body {padding: 0 20px;}
.usernamage /deep/ .el-button--text{padding: 0;}
.usernamage /deep/ .el-form-item{margin-bottom: 0;}
.tablepage{text-align: right;}
.padingb10{padding-bottom: 18px;}
.usernamage  /deep/ .el-form-item__label{ text-align: right;text-align-last: inherit;}
.tabledata /deep/ .el-button--text{padding: 0;}
.usernamage /deep/ .el-table td, .el-table th{padding: 3px 0;}
.block{display: flex;}
.usernamage{padding: 10px;}
.div-a{ float:left;width:13%;}
.div-b{ float:left;width:87%;}
</style>

