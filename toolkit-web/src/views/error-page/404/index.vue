<template>
  <div class="errPage-container">
    <img :src="errGif0" class="imgStyle" width="300" height="200">
    <div />
    <h1 class="text-jumbo">Oops! {{ $t('errorPage.noPage') }}</h1>
    <el-button
      icon="el-icon-arrow-left"
      class="pan-back-btn"
      @click="back"
    >{{ $t('errorPage.back') }}</el-button>
  </div>
</template>

<script>
import errGif0 from '@/assets/404_images/404.gif'
import { removeToken } from '@/utils/auth'

export default {
  name: 'Page403',
  data() {
    return {
      errGif0: errGif0 + '?' + +new Date()
    }
  },
  methods: {
    back() {
      removeToken()
      this.$router.push({ path: '/login' })
    }
  }
}
</script>

<style lang="scss" scoped>
.errPage-container {
  max-width: 100%;
  text-align: center;
  .pan-back-btn {
    background: #008489;
    color: #fff;
    border: none !important;
  }
  .imgStyle {
    margin-left: 30px;
    margin-top: 100px;
  }
  .text-jumbo {
    font-size: 30px;
    font-weight: 600;
    color: #484848;
    margin-top: 30px;
  }
}
</style>
